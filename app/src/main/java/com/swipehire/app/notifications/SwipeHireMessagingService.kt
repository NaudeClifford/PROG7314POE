package com.swipehire.app.notifications

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.swipehire.app.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SwipeHireMessagingService : FirebaseMessagingService() {
    private companion object { const val TAG = "SwipeHirePush" }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed; requesting registration")
        FcmTokenManager.syncAsync(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val signedInUser = FirebaseAuth.getInstance().currentUser?.uid
        if (signedInUser == null || signedInUser != data["userId"]) {
            Log.d(TAG, "Push ignored because its account does not match the active session")
            return
        }

        try {
            runBlocking(Dispatchers.IO) {
                val settings = SettingsRepository(applicationContext).state.first()
                if (PushNotificationPolicy.shouldDisplay(
                    type = data["type"].orEmpty(),
                    pushNotifications = settings.pushNotifications,
                    matchAlerts = settings.matchAlerts,
                    messageAlerts = settings.messageAlerts
                    )
                ) {
                    SwipeHireNotificationManager.show(applicationContext, data, settings.language)
                    Log.d(TAG, "Push displayed")
                } else {
                    Log.d(TAG, "Push suppressed by notification preferences")
                }
            }
        } catch (error: Exception) {
            Log.w(TAG, "Push could not be displayed: ${error.javaClass.simpleName}")
        }
    }

}
