package com.swipehire.app

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swipehire.app.data.currentFirebaseUserId
import com.swipehire.app.data.signOutFirebaseUser
import com.swipehire.app.security.BiometricSession
import com.swipehire.app.ui.theme.SwipeHireTheme
import com.swipehire.app.ui.LocalAppLanguage
import com.swipehire.app.ui.screens.BiometricLockScreen
import com.swipehire.app.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.state.collectAsState()
            val biometricUnlocked by BiometricSession.unlocked.collectAsState()
            val scope = rememberCoroutineScope()

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
        if (!isChangingConfigurations) BiometricSession.lock()
    }
}
