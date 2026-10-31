package com.swipehire.app.security

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private const val AUTHENTICATORS =
    BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.BIOMETRIC_WEAK

/** Small wrapper around Android's native fingerprint/face prompt. */
class BiometricAuthenticator(private val activity: FragmentActivity) {

    fun unavailableReason(): String? = when (
        BiometricManager.from(activity).canAuthenticate(AUTHENTICATORS)
    ) {
        BiometricManager.BIOMETRIC_SUCCESS -> null
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "This device has no biometric hardware."
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric authentication is temporarily unavailable."
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "Add a fingerprint or face in the device settings first."
        BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> "A device security update is required before biometrics can be used."
        else -> "Biometric authentication is not available on this device."
    }

    fun authenticate(
        title: String,
        subtitle: String,
        cancelText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        unavailableReason()?.let {
            onError(it)
            return
        }

        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Fingerprint or face not recognised. Try again.")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(AUTHENTICATORS)
            .setNegativeButtonText(cancelText)
            .build()
        prompt.authenticate(promptInfo)
    }
}

tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
