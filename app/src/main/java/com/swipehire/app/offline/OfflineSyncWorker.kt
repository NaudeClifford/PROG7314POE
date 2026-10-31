package com.swipehire.app.offline

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.swipehire.app.data.currentFirebaseUserId
import com.swipehire.app.data.remote.RetrofitClient
import com.swipehire.app.data.remote.SetSavedItemRequest
import kotlinx.coroutines.CancellationException

class OfflineSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        OfflineSyncManager.initialise(applicationContext)
        val userId = currentFirebaseUserId()
            ?: return if (OfflineSyncManager.hasPendingActions()) Result.retry() else Result.success()
        val actions = OfflineSyncManager.actionsFor(userId)
        if (actions.isEmpty()) return Result.success()

        return try {
            actions.forEach { action ->
                when (action.type) {
                    OfflineActionType.SET_SAVED_ITEM -> {
                        val payload = OfflineSyncManager.decodeSaved(action)
                        val snapshot = RetrofitClient.api.setSavedItem(
                            action.userId,
                            payload.kind,
                            payload.itemId,
                            SetSavedItemRequest(payload.saved)
                        )
                        OfflineSyncManager.complete(action, snapshot)
                    }

                    OfflineActionType.UPDATE_SETTINGS -> {
                        val payload = OfflineSyncManager.decodeSettings(action)
                        RetrofitClient.api.updateUserSettings(action.userId, payload.settings)
                        OfflineSyncManager.complete(action)
                    }
                }
            }
            Log.i("OfflineSync", "Synchronised ${actions.size} queued action(s).")
            Result.success()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w("OfflineSync", "Queued changes are still waiting for a connection.", error)
            Result.retry()
        }
    }
}
