package com.swipehire.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.swipehire.app.data.AccountType
import com.swipehire.app.data.AppLanguage
import com.swipehire.app.data.SettingsRepository
import com.swipehire.app.data.SettingsState
import com.swipehire.app.data.currentFirebaseUserId
import com.swipehire.app.data.remote.UserSettingsDto
import com.swipehire.app.data.repository.AppRepository
import com.swipehire.app.offline.OfflineSyncManager
import com.swipehire.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository = SettingsRepository(application)
    private val appRepository = AppRepository()
    private val remoteSettings = MutableStateFlow<UserSettingsDto?>(null)
    private val remoteAccountType = MutableStateFlow<AccountType?>(null)
    val pendingOfflineActions = OfflineSyncManager.pendingCount

    val state: StateFlow<SettingsState> = combine(localRepository.state, remoteSettings, remoteAccountType) { local, remote, role ->
        val accountSettings = remote ?: UserSettingsDto(
            pushNotifications = local.pushNotifications,
            matchAlerts = local.matchAlerts,
            messageAlerts = local.messageAlerts,
            profileVisible = local.profileVisible,
            language = local.language.code
        )
        local.copy(
            accountType = role ?: local.accountType,
            pushNotifications = accountSettings.pushNotifications,
            matchAlerts = accountSettings.matchAlerts,
            messageAlerts = accountSettings.messageAlerts,
            profileVisible = accountSettings.profileVisible,
            language = local.language
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState()
    )

    init {
        refreshSession()
    }

    fun refreshSession() = viewModelScope.launch {
        val userId = currentFirebaseUserId() ?: return@launch
        remoteAccountType.value = appRepository.getAccountType(userId)
        appRepository.getUserSettings(userId)?.let {
            remoteSettings.value = it
            localRepository.setLanguage(AppLanguage.fromCode(it.language))
            localRepository.setAccountPreferences(
                it.pushNotifications,
                it.matchAlerts,
                it.messageAlerts,
                it.profileVisible
            )
        }
    }

    fun chooseAccountType(type: AccountType, onSaved: (Boolean) -> Unit = {}) = viewModelScope.launch {
        val userId = currentFirebaseUserId()
        val saved = userId != null && appRepository.setAccountType(userId, type)
        if (saved) {
            remoteAccountType.value = type
            localRepository.setOnboarded(true)
        }
        onSaved(saved)
    }

    fun resolveSignedInAccount(onResolved: (AccountType?) -> Unit) = viewModelScope.launch {
        val userId = currentFirebaseUserId()
        val role = userId?.let { appRepository.getAccountType(it) }
        if (role != null) {
            remoteAccountType.value = role
            localRepository.setOnboarded(true)
        }
        onResolved(role)
    }

    fun logOut() = viewModelScope.launch {
        FirebaseAuth.getInstance().signOut()
        remoteAccountType.value = null
        remoteSettings.value = null
        localRepository.setOnboarded(false)
    }
    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { localRepository.setThemeMode(mode) }
    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        localRepository.setLanguage(language)
        val userId = currentFirebaseUserId() ?: return@launch
        val previous = currentRemoteSettings()
        val updated = previous.copy(language = language.code)
        remoteSettings.value = updated
        if (appRepository.updateUserSettings(userId, updated) == null) remoteSettings.value = previous
    }
    fun setBiometricLock(enabled: Boolean) = viewModelScope.launch { localRepository.setBiometricLock(enabled) }
    fun setOnboarded(done: Boolean) = viewModelScope.launch { localRepository.setOnboarded(done) }

    fun setPushNotifications(enabled: Boolean) = updateRemote { it.copy(pushNotifications = enabled) }
    fun setMatchAlerts(enabled: Boolean) = updateRemote { it.copy(matchAlerts = enabled) }
    fun setMessageAlerts(enabled: Boolean) = updateRemote { it.copy(messageAlerts = enabled) }
    fun setProfileVisible(visible: Boolean) = updateRemote { it.copy(profileVisible = visible) }

    private fun updateRemote(transform: (UserSettingsDto) -> UserSettingsDto) {
        val userId = currentFirebaseUserId() ?: return
        val previous = currentRemoteSettings()
        val updated = transform(previous)
        remoteSettings.value = updated
        viewModelScope.launch {
            localRepository.setAccountPreferences(
                updated.pushNotifications,
                updated.matchAlerts,
                updated.messageAlerts,
                updated.profileVisible
            )
            val persisted = appRepository.updateUserSettings(userId, updated)
            if (persisted != null) {
                remoteSettings.value = persisted
            } else {
                remoteSettings.value = previous
                localRepository.setAccountPreferences(
                    previous.pushNotifications,
                    previous.matchAlerts,
                    previous.messageAlerts,
                    previous.profileVisible
                )
            }
        }
    }

    private fun currentRemoteSettings(): UserSettingsDto {
        remoteSettings.value?.let { return it }
        val current = state.value
        return UserSettingsDto(
            pushNotifications = current.pushNotifications,
            matchAlerts = current.matchAlerts,
            messageAlerts = current.messageAlerts,
            profileVisible = current.profileVisible,
            language = current.language.code
        )
    }
}
