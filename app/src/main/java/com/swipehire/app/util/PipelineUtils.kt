package com.swipehire.app.util

import com.swipehire.app.data.AccountType
import com.swipehire.app.data.Interview
import com.swipehire.app.data.InterviewStatus
import com.swipehire.app.data.PipelineStage

object PipelineRules {
    fun actionsFor(role: AccountType, from: PipelineStage): Set<PipelineStage> = when (role) {
        AccountType.COMPANY -> when (from) {
            PipelineStage.MATCHED -> setOf(PipelineStage.INTERVIEW, PipelineStage.DECLINED)
            PipelineStage.INTERVIEW -> setOf(PipelineStage.OFFER, PipelineStage.DECLINED)
            PipelineStage.OFFER -> setOf(PipelineStage.DECLINED)
            else -> emptySet()
        }
        AccountType.STUDENT -> when (from) {
            PipelineStage.MATCHED, PipelineStage.INTERVIEW -> setOf(PipelineStage.DECLINED)
            PipelineStage.OFFER -> setOf(PipelineStage.HIRED, PipelineStage.DECLINED)
            else -> emptySet()
        }
    }

    fun canMove(role: AccountType, from: PipelineStage, to: PipelineStage): Boolean =
        to in actionsFor(role, from)

    fun stageAfterInterviewAccepted(current: PipelineStage): PipelineStage =
        if (current == PipelineStage.MATCHED) PipelineStage.INTERVIEW else current

    fun canSchedule(stage: PipelineStage): Boolean =
        stage == PipelineStage.MATCHED || stage == PipelineStage.INTERVIEW

    fun timelineIndex(stage: PipelineStage): Int = when (stage) {
        PipelineStage.MATCHED -> 0
        PipelineStage.INTERVIEW -> 1
        PipelineStage.OFFER -> 2
        PipelineStage.HIRED -> 3
        PipelineStage.DECLINED -> -1
    }
}

object InterviewRules {
    const val MIN_LEAD_MS = 60 * 60 * 1000L
    const val MIN_DURATION_MIN = 15
    const val MAX_DURATION_MIN = 120
    const val MAX_NOTE_LENGTH = 200

    fun validate(startAtMillis: Long, nowMillis: Long, durationMinutes: Int, existing: List<Interview>): String? = when {
        startAtMillis < nowMillis + MIN_LEAD_MS -> "Pick a time at least 1 hour from now"
        durationMinutes !in MIN_DURATION_MIN..MAX_DURATION_MIN -> "Duration must be 15–120 minutes"
        conflicts(startAtMillis, durationMinutes, existing) -> "This time clashes with another interview"
        existing.any { it.status == InterviewStatus.PROPOSED } -> "Wait for a reply to your current proposal"
        else -> null
    }

    fun conflicts(startAtMillis: Long, durationMinutes: Int, existing: List<Interview>): Boolean {
        val end = startAtMillis + durationMinutes * 60_000L
        return existing
            .filter { it.status == InterviewStatus.PROPOSED || it.status == InterviewStatus.ACCEPTED }
            .any { other ->
                val otherEnd = other.startAtMillis + other.durationMinutes * 60_000L
                startAtMillis < otherEnd && other.startAtMillis < end
            }
    }
}