package com.swipehire.app.notifications

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

/** Registers this Firebase installation beneath the signed-in user. */
object FcmTokenManager {
    private const val TAG = "FcmTokenManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun syncAsync(refreshedToken: String? = null) {
        scope.launch {
            runCatching { syncCurrentDevice(refreshedToken) }
                .onFailure { Log.e(TAG, "Unable to register this installation for push notifications", it) }
        }
    }

    suspend fun syncCurrentDevice(refreshedToken: String? = null) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val installationId = FirebaseInstallations.getInstance().id.await()
        val token = refreshedToken ?: FirebaseMessaging.getInstance().token.await()
        val deviceId = installationId.sha256()

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .collection("devices")
            .document(deviceId)
            .set(
                mapOf(
                    "fid" to installationId,
                    "token" to token,
                    "platform" to "android",
                    "enabled" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()
    }

    suspend fun unregisterCurrentDevice() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val installationId = runCatching { FirebaseInstallations.getInstance().id.await() }.getOrNull()
        val deviceId = installationId?.sha256() ?: return
        runCatching {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("devices")
                .document(deviceId)
                .delete()
                .await()
        }.onFailure { Log.w(TAG, "Unable to unregister this installation during sign out", it) }
    }

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray(Charsets.UTF_8))
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
}
