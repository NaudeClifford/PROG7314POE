package com.swipehire.app.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.swipehire.app.data.remote.SavedItemsDto
import com.swipehire.app.data.remote.UserSettingsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

object OfflineSyncManager {
    private const val WORK_NAME = "swipehire-offline-sync"
    private val gson = Gson()
    private lateinit var appContext: Context
    private lateinit var store: OfflineActionStore
    private var initialised = false

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount = _pendingCount.asStateFlow()

    @Synchronized
    fun initialise(context: Context) {
        if (!initialised) {
            appContext = context.applicationContext
            store = OfflineActionStore(appContext)
            initialised = true
            Log.i("OfflineSync", "Local offline store initialised")
        }
        refreshCount()
        schedule()
    }

    fun enqueueSavedItem(userId: String, kind: String, itemId: String, saved: Boolean): SavedItemsDto {
        ensureInitialised()
        val payload = SavedItemActionPayload(kind, itemId, saved)
        store.enqueue(userId, OfflineActionType.SET_SAVED_ITEM, "$kind:$itemId", gson.toJson(payload))
        store.cacheSavedChoice(userId, kind, itemId, saved)
        refreshCount()
        Log.d("OfflineSync", "Saved-item change queued; pending=${_pendingCount.value}")
        schedule()
        return store.cachedSavedItems(userId)
    }

    fun enqueueSettings(userId: String, settings: UserSettingsDto) {
        ensureInitialised()
        store.enqueue(
            userId,
            OfflineActionType.UPDATE_SETTINGS,
            "settings",
            gson.toJson(SettingsActionPayload(settings))
        )
        refreshCount()
        Log.d("OfflineSync", "Settings change queued; pending=${_pendingCount.value}")
        schedule()
    }

    fun cacheSavedItems(userId: String, snapshot: SavedItemsDto) {
        ensureInitialised()
        store.cacheServerSavedItems(userId, snapshot)
    }

    fun cachedSavedItems(userId: String): SavedItemsDto {
        ensureInitialised()
        return store.cachedSavedItems(userId)
    }

    fun pendingSettingsFor(userId: String): UserSettingsDto? {
        ensureInitialised()
        return store.pendingActions(userId)
            .firstOrNull { it.type == OfflineActionType.UPDATE_SETTINGS }
            ?.let { decodeSettings(it).settings }
    }

    fun isNetworkAvailable(): Boolean {
        ensureInitialised()
        val manager = appContext.getSystemService(ConnectivityManager::class.java)
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    internal fun actionsFor(userId: String): List<PendingOfflineAction> {
        ensureInitialised()
        return store.pendingActions(userId)
    }

    internal fun hasPendingActions(): Boolean = _pendingCount.value > 0

    internal fun decodeSaved(action: PendingOfflineAction): SavedItemActionPayload =
        gson.fromJson(action.payload, SavedItemActionPayload::class.java)

    internal fun decodeSettings(action: PendingOfflineAction): SettingsActionPayload =
        gson.fromJson(action.payload, SettingsActionPayload::class.java)

    internal fun complete(action: PendingOfflineAction, savedSnapshot: SavedItemsDto? = null) {
        ensureInitialised()
        store.delete(action)
        if (savedSnapshot != null) store.cacheServerSavedItems(action.userId, savedSnapshot)
        refreshCount()
    }

    fun schedule() {
        if (!initialised) return
        val request = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
    }

    private fun refreshCount() {
        if (initialised) _pendingCount.value = store.pendingCount()
    }

    private fun ensureInitialised() {
        check(initialised) { "OfflineSyncManager must be initialised from MainApplication." }
    }
}
