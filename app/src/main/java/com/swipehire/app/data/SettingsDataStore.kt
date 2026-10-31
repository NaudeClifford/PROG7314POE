package com.swipehire.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.swipehire.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsStore by preferencesDataStore(name = "swipehire_settings")

/** Combined UI state. Account preferences are supplied from Firestore by SettingsViewModel. */
data class SettingsState(
    val accountType: AccountType = AccountType.STUDENT,
    val pushNotifications: Boolean = true,
    val matchAlerts: Boolean = true,
    val messageAlerts: Boolean = true,
    val profileVisible: Boolean = true,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val biometricLock: Boolean = false,
    val onboarded: Boolean = false,
    val isLoaded: Boolean = false
)

private object Keys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val LANGUAGE = stringPreferencesKey("language")
    val BIOMETRIC = booleanPreferencesKey("biometric_lock")
    val ONBOARDED = booleanPreferencesKey("onboarded")
    val PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications")
    val MATCH_ALERTS = booleanPreferencesKey("match_alerts")
    val MESSAGE_ALERTS = booleanPreferencesKey("message_alerts")
    val PROFILE_VISIBLE = booleanPreferencesKey("profile_visible")
}

class SettingsRepository(private val context: Context) {

    val state: Flow<SettingsState> = context.settingsStore.data.map { prefs ->
        SettingsState(
            pushNotifications = prefs[Keys.PUSH_NOTIFICATIONS] ?: true,
            matchAlerts = prefs[Keys.MATCH_ALERTS] ?: true,
            messageAlerts = prefs[Keys.MESSAGE_ALERTS] ?: true,
            profileVisible = prefs[Keys.PROFILE_VISIBLE] ?: true,
            language = AppLanguage.fromCode(prefs[Keys.LANGUAGE]),
            themeMode = prefs[Keys.THEME_MODE]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM,
            biometricLock = prefs[Keys.BIOMETRIC] ?: false,
            onboarded = prefs[Keys.ONBOARDED] ?: false,
            isLoaded = true
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.settingsStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.BIOMETRIC] = enabled }
    }

    suspend fun setOnboarded(done: Boolean) {
        context.settingsStore.edit { it[Keys.ONBOARDED] = done }
    }

    suspend fun setAccountPreferences(
        pushNotifications: Boolean,
        matchAlerts: Boolean,
        messageAlerts: Boolean,
        profileVisible: Boolean
    ) {
        context.settingsStore.edit {
            it[Keys.PUSH_NOTIFICATIONS] = pushNotifications
            it[Keys.MATCH_ALERTS] = matchAlerts
            it[Keys.MESSAGE_ALERTS] = messageAlerts
            it[Keys.PROFILE_VISIBLE] = profileVisible
        }
    }
}
