package com.swipehire.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swipehire.app.data.currentFirebaseUserId
import com.swipehire.app.data.signOutFirebaseUser
import com.swipehire.app.notifications.FcmTokenManager
import com.swipehire.app.security.BiometricSession
import com.swipehire.app.ui.theme.SwipeHireTheme
import com.swipehire.app.ui.LocalAppLanguage
import com.swipehire.app.ui.screens.BiometricLockScreen
import com.swipehire.app.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity created; restoring=${savedInstanceState != null}")

        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.state.collectAsState()
            val biometricUnlocked by BiometricSession.unlocked.collectAsState()
            val scope = rememberCoroutineScope()
            val notificationPermission = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                Log.i(TAG, "Notification permission granted=$granted")
                if (granted) {
                    FcmTokenManager.syncAsync()
                } else {
                    settingsViewModel.setPushNotifications(false)
                }
            }

            LaunchedEffect(
                settingsState.isLoaded,
                settingsState.onboarded,
                settingsState.pushNotifications,
                biometricUnlocked
            ) {
                val mayPrompt = settingsState.isLoaded &&
                    settingsState.onboarded &&
                    settingsState.pushNotifications &&
                    currentFirebaseUserId() != null &&
                    (!settingsState.biometricLock || biometricUnlocked)
                if (!mayPrompt) return@LaunchedEffect

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    FcmTokenManager.syncAsync()
                } else {
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            CompositionLocalProvider(LocalAppLanguage provides settingsState.language) {
                SwipeHireTheme(themeMode = settingsState.themeMode) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        when {
                            !settingsState.isLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }

                            settingsState.biometricLock && currentFirebaseUserId() != null && !biometricUnlocked ->
                                BiometricLockScreen(
                                    onUnlocked = BiometricSession::unlock,
                                    onUseAccountSignIn = {
                                        scope.launch {
                                            settingsViewModel.setBiometricLock(false)
                                            signOutFirebaseUser(this@MainActivity)
                                            settingsViewModel.logOut()
                                        }
                                    }
                                )

                            else -> SwipeHireApp(settingsViewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            BiometricSession.lock()
            Log.d(TAG, "Activity stopped; biometric session locked")
        }
    }

    private companion object {
        const val TAG = "SwipeHireActivity"
    }
}
