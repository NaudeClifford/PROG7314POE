package com.swipehire.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swipehire.app.security.BiometricAuthenticator
import com.swipehire.app.security.findFragmentActivity
import com.swipehire.app.ui.tr

@Composable
fun BiometricLockScreen(onUnlocked: () -> Unit, onUseAccountSignIn: () -> Unit) {
    val context = LocalContext.current
    val activity = context.findFragmentActivity()
    var error by remember { mutableStateOf<String?>(null) }
    var promptShown by remember { mutableStateOf(false) }
    val promptTitle = tr("Unlock SwipeHire")
    val promptSubtitle = tr("Confirm your fingerprint or face to continue")
    val cancelText = tr("Cancel")

    fun showPrompt() {
        val host = activity
        if (host == null) {
            error = "Biometric authentication could not be opened."
            return
        }
        error = null
        BiometricAuthenticator(host).authenticate(
            title = promptTitle,
            subtitle = promptSubtitle,
            cancelText = cancelText,
            onSuccess = onUnlocked,
            onError = { error = it }
        )
    }

    LaunchedEffect(activity) {
        if (activity != null && !promptShown) {
            promptShown = true
            showPrompt()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(tr("SwipeHire is locked"), style = MaterialTheme.typography.headlineSmall)
        Text(
            tr("Use the fingerprint or face enrolled on this device to continue."),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        error?.let {
            Text(tr(it), color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
        }
        Button(onClick = ::showPrompt) { Text(tr("Unlock with biometrics")) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onUseAccountSignIn) { Text(tr("Use account sign-in")) }
    }
}
