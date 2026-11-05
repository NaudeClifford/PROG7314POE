package com.swipehire.app.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.swipehire.app.data.AccountType
import com.swipehire.app.data.AlertType
import com.swipehire.app.data.Interview
import com.swipehire.app.data.InterviewMode
import com.swipehire.app.data.InterviewStatus
import com.swipehire.app.data.PipelineStage
import com.swipehire.app.util.PipelineRules
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Firestore access for the interview scheduler (feature 4) and the application
 * pipeline tracker (feature 5).
 *
 * Data layout:
 *   matches/{matchId}.stage, .stageUpdatedAt            current pipeline stage
 *   matches/{matchId}/interviews/{interviewId}          one document per proposal
 *   notifications/{id}                                  alert for the other participant
 *
 * Every write also creates a `notifications` document, so the real-time push
 * (FCM) work picks interview and stage changes up with no extra wiring.
 */
class PipelineRepository {

    private companion object {
        const val TAG = "PipelineRepo"
    }

    private val db: FirebaseFirestore?
        get() = try { FirebaseFirestore.getInstance() } catch (e: Exception) {
            Log.e(TAG, "Firestore unavailable", e); null
        }

    /** Reads users/{uid}.role; null when the user has not chosen a role yet. */
    suspend fun getRole(userId: String): AccountType? = try {
        db?.collection("users")?.document(userId)?.get()?.await()?.getString("role")
            ?.let { runCatching { AccountType.valueOf(it) }.getOrNull() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(TAG, "getRole failed", e); null
    }

    /** Live pipeline stage of one match. Emits MATCHED until the field exists. */
    fun observeStage(matchId: String): Flow<PipelineStage> = callbackFlow {
        val firestore = db
        if (firestore == null) { close(); return@callbackFlow }
        val listener = firestore.collection("matches").document(matchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Stage listener failed", error)
                    return@addSnapshotListener
                }
                trySend(PipelineStage.fromName(snapshot?.getString("stage")))
            }
        awaitClose { listener.remove() }
    }

    /** Live list of interviews for one match, soonest first. */
    fun observeInterviews(matchId: String): Flow<List<Interview>> = callbackFlow {
        val firestore = db
        if (firestore == null) { trySend(emptyList()); close(); return@callbackFlow }
        val listener = firestore.collection("matches").document(matchId)
            .collection("interviews").orderBy("startAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Interview listener failed", error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().mapNotNull { it.toInterview(matchId) })
            }
        awaitClose { listener.remove() }
    }

    private fun DocumentSnapshot.toInterview(matchId: String): Interview? {
        val start = getTimestamp("startAt") ?: return null // malformed record: skip it
        return Interview(
            id = id,
            matchId = matchId,
            proposedBy = getString("proposedBy").orEmpty(),
            startAtMillis = start.toDate().time,
            durationMinutes = (getLong("durationMinutes") ?: 30L).toInt(),
            mode = InterviewMode.fromName(getString("mode")),
            note = getString("note").orEmpty(),
            status = InterviewStatus.entries.firstOrNull { it.name == getString("status") }
                ?: InterviewStatus.PROPOSED
        )
    }

    suspend fun proposeInterview(
        matchId: String, proposerId: String, startAtMillis: Long,
        durationMinutes: Int, mode: InterviewMode, note: String
    ): Boolean {
        val firestore = db ?: return false
        return try {
            val matchRef = firestore.collection("matches").document(matchId)
            val interviewRef = matchRef.collection("interviews").document()
            val notificationRef = firestore.collection("notifications").document()
            firestore.runTransaction { transaction ->
                val match = transaction.get(matchRef)
                val role = transaction.get(firestore.collection("users").document(proposerId)).getString("role")
                check(role == AccountType.COMPANY.name) { "Only a company may propose an interview." }
                val other = otherParticipant(match, proposerId)
                    ?: error("The proposer is not a match participant.")
                transaction.set(
                    interviewRef,
                    mapOf(
                    "proposedBy" to proposerId,
                    "startAt" to Timestamp(Date(startAtMillis)),
                    "durationMinutes" to durationMinutes,
                    "mode" to mode.name,
                    "note" to note.trim().take(200),
                    "status" to InterviewStatus.PROPOSED.name,
                    "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                transaction.set(
                    notificationRef,
                    notificationData(
                        other,
                        proposerId,
                        AlertType.INTERVIEW,
                        "Interview proposed",
                        describeSlot(startAtMillis, mode),
                        matchId
                    )
                )
            }.await()
            Log.d(TAG, "Interview proposed")
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "proposeInterview failed", e); false
        }
    }

    /** The non-proposer accepts or declines. Accepting also advances a fresh match to INTERVIEW. */
    suspend fun respondToInterview(matchId: String, interviewId: String, responderId: String, accept: Boolean): Boolean {
        val firestore = db ?: return false
        return try {
            val matchRef = firestore.collection("matches").document(matchId)
            val ref = matchRef.collection("interviews").document(interviewId)
            val notificationRef = firestore.collection("notifications").document()
            val newStatus = if (accept) InterviewStatus.ACCEPTED else InterviewStatus.DECLINED
            firestore.runTransaction { transaction ->
                val interview = transaction.get(ref).toInterview(matchId)
                    ?: error("Interview not found.")
                val match = transaction.get(matchRef)
                val participants = (match.get("participantIds") as? List<*>)?.filterIsInstance<String>().orEmpty()
                check(responderId in participants) { "Responder is not a match participant." }
                check(interview.proposedBy != responderId && interview.status == InterviewStatus.PROPOSED) {
                    "Interview cannot be answered in its current state."
                }
                transaction.update(
                    ref,
                    mapOf("status" to newStatus.name, "respondedAt" to FieldValue.serverTimestamp())
                )
                if (accept) {
                    val current = PipelineStage.fromName(match.getString("stage"))
                    val next = PipelineRules.stageAfterInterviewAccepted(current)
                    if (next != current) {
                        transaction.update(
                            matchRef,
                            mapOf("stage" to next.name, "stageUpdatedAt" to FieldValue.serverTimestamp())
                        )
                    }
                }
                transaction.set(
                    notificationRef,
                    notificationData(
                        interview.proposedBy,
                        responderId,
                        AlertType.INTERVIEW,
                        if (accept) "Interview accepted" else "Interview declined",
                        describeSlot(interview.startAtMillis, interview.mode),
                        matchId
                    )
                )
            }.await()
            Log.d(TAG, "Interview response saved: $newStatus")
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "respondToInterview failed", e); false
        }
    }

    /** Only the proposer may cancel, and only while the proposal is still pending or accepted. */
    suspend fun cancelInterview(matchId: String, interviewId: String, actorId: String): Boolean {
        val firestore = db ?: return false
        return try {
            val matchRef = firestore.collection("matches").document(matchId)
            val ref = matchRef.collection("interviews").document(interviewId)
            val notificationRef = firestore.collection("notifications").document()
            firestore.runTransaction { transaction ->
                val interview = transaction.get(ref).toInterview(matchId)
                    ?: error("Interview not found.")
                val match = transaction.get(matchRef)
                check(
                    interview.proposedBy == actorId &&
                        interview.status in setOf(InterviewStatus.PROPOSED, InterviewStatus.ACCEPTED)
                ) { "Interview cannot be cancelled in its current state." }
                val other = otherParticipant(match, actorId)
                    ?: error("The actor is not a match participant.")
                transaction.update(
                    ref,
                    mapOf("status" to InterviewStatus.CANCELLED.name, "respondedAt" to FieldValue.serverTimestamp())
                )
                transaction.set(
                    notificationRef,
                    notificationData(
                        other,
                        actorId,
                        AlertType.INTERVIEW,
                        "Interview cancelled",
                        describeSlot(interview.startAtMillis, interview.mode),
                        matchId
                    )
                )
            }.await()
            Log.d(TAG, "Interview cancelled")
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "cancelInterview failed", e); false
        }
    }

    /** Moves the match to [newStage] if [role] is allowed to make that transition from the stored stage. */
    suspend fun updateStage(matchId: String, actorId: String, newStage: PipelineStage): Boolean {
        val firestore = db ?: return false
        return try {
            val matchRef = firestore.collection("matches").document(matchId)
            val notificationRef = firestore.collection("notifications").document()
            var previousStage = PipelineStage.MATCHED
            var verifiedRole = AccountType.STUDENT
            firestore.runTransaction { transaction ->
                val match = transaction.get(matchRef)
                val roleName = transaction.get(firestore.collection("users").document(actorId)).getString("role")
                val role = AccountType.entries.firstOrNull { it.name == roleName }
                    ?: error("User role is not configured.")
                val participants = (match.get("participantIds") as? List<*>)?.filterIsInstance<String>().orEmpty()
                check(actorId in participants && match.getBoolean("mutual") == true) {
                    "Only mutual-match participants may update the pipeline."
                }
                val current = PipelineStage.fromName(match.getString("stage"))
                check(PipelineRules.canMove(role, current, newStage)) {
                    "Pipeline transition is not permitted for this role."
                }
                val other = otherParticipant(match, actorId)
                    ?: error("The actor is not a match participant.")
                previousStage = current
                verifiedRole = role
                transaction.update(
                    matchRef,
                    mapOf("stage" to newStage.name, "stageUpdatedAt" to FieldValue.serverTimestamp())
                )
                transaction.set(
                    notificationRef,
                    notificationData(
                        other,
                        actorId,
                        AlertType.STAGE,
                        "Application status updated",
                        newStage.label,
                        matchId
                    )
                )
            }.await()
            Log.d(TAG, "Application stage changed: $previousStage -> $newStage by $verifiedRole")
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "updateStage failed", e); false
        }
    }

    private fun otherParticipant(match: DocumentSnapshot, me: String): String? =
        (match.get("participantIds") as? List<*>)?.filterIsInstance<String>()?.firstOrNull { it != me }

    private fun describeSlot(startAtMillis: Long, mode: InterviewMode): String =
        SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(Date(startAtMillis)) + " · " + mode.label

    /** Same document shape as FirestoreRepository.createNotification so AlertsViewModel reads it unchanged. */
    private fun notificationData(
        userId: String, actorId: String, type: AlertType, title: String, body: String, matchId: String
    ): Map<String, Any> = mapOf(
        "userId" to userId,
        "actorId" to actorId,
        "type" to type.name,
        "title" to title,
        "body" to body,
        "matchId" to matchId,
        "createdAt" to FieldValue.serverTimestamp(),
        "read" to false
    )
}
