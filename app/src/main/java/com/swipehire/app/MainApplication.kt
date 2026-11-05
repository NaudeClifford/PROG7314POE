package com.swipehire.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.swipehire.app.notifications.FcmTokenManager
import com.swipehire.app.notifications.SwipeHireNotificationManager
import com.swipehire.app.offline.OfflineSyncManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        SwipeHireNotificationManager.createChannel(this)
        OfflineSyncManager.initialise(this)
        Log.i("SwipeHireApp", "Application services initialised")
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                Log.d("SwipeHireApp", "Authenticated session active; scheduling sync and token registration")
                OfflineSyncManager.schedule()
                FcmTokenManager.syncAsync()
            } else {
                Log.d("SwipeHireApp", "No authenticated session")
            }
        }
    }
}
