package com.swipehire.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.swipehire.app.offline.OfflineSyncManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        OfflineSyncManager.initialise(this)
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) OfflineSyncManager.schedule()
        }
    }
}
