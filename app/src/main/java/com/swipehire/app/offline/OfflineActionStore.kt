package com.swipehire.app.offline

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.swipehire.app.data.remote.SavedItemsDto
import com.swipehire.app.data.remote.UserSettingsDto

enum class OfflineActionType { SET_SAVED_ITEM, UPDATE_SETTINGS }

data class PendingOfflineAction(
    val userId: String,
    val type: OfflineActionType,
    val dedupeKey: String,
    val payload: String
)

data class SavedItemActionPayload(val kind: String, val itemId: String, val saved: Boolean)
data class SettingsActionPayload(val settings: UserSettingsDto)

/**
 * Small SQLite-backed outbox used by the saved-items and settings features.
 * Upserts coalesce repeated offline taps, so only the user's final choice is synced.
 */
class OfflineActionStore(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    "swipehire_offline.db",
    null,
    1
) {
    private val gson = Gson()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE pending_actions (
                user_id TEXT NOT NULL,
                action_type TEXT NOT NULL,
                dedupe_key TEXT NOT NULL,
                payload TEXT NOT NULL,
                updated_at INTEGER NOT NULL,
                PRIMARY KEY (user_id, action_type, dedupe_key)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE saved_items (
                user_id TEXT NOT NULL,
                kind TEXT NOT NULL,
                item_id TEXT NOT NULL,
                saved INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                PRIMARY KEY (user_id, kind, item_id)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    @Synchronized
    fun enqueue(userId: String, type: OfflineActionType, dedupeKey: String, payload: String) {
        writableDatabase.insertWithOnConflict(
            "pending_actions",
            null,
            ContentValues().apply {
                put("user_id", userId)
                put("action_type", type.name)
                put("dedupe_key", dedupeKey)
                put("payload", payload)
                put("updated_at", System.currentTimeMillis())
            },
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    @Synchronized
    fun cacheSavedChoice(userId: String, kind: String, itemId: String, saved: Boolean) {
        writableDatabase.insertWithOnConflict(
            "saved_items",
            null,
            ContentValues().apply {
                put("user_id", userId)
                put("kind", kind)
                put("item_id", itemId)
                put("saved", if (saved) 1 else 0)
                put("updated_at", System.currentTimeMillis())
            },
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    @Synchronized
    fun cacheServerSavedItems(userId: String, snapshot: SavedItemsDto) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("saved_items", "user_id = ?", arrayOf(userId))
            snapshot.savedJobIds.forEach { cacheSavedChoice(userId, "job", it, true) }
            snapshot.savedStudentIds.forEach { cacheSavedChoice(userId, "student", it, true) }

            // Keep unsynchronised local choices on top of the latest server snapshot.
            pendingActions(userId)
                .filter { it.type == OfflineActionType.SET_SAVED_ITEM }
                .map { gson.fromJson(it.payload, SavedItemActionPayload::class.java) }
                .forEach { cacheSavedChoice(userId, it.kind, it.itemId, it.saved) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    @Synchronized
    fun cachedSavedItems(userId: String): SavedItemsDto {
        val jobs = mutableListOf<String>()
        val students = mutableListOf<String>()
        readableDatabase.query(
            "saved_items",
            arrayOf("kind", "item_id", "saved"),
            "user_id = ?",
            arrayOf(userId),
            null,
            null,
            "updated_at ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                if (cursor.getInt(2) != 1) continue
                when (cursor.getString(0)) {
                    "job" -> jobs += cursor.getString(1)
                    "student" -> students += cursor.getString(1)
                }
            }
        }
        return SavedItemsDto(jobs, students)
    }

    @Synchronized
    fun pendingActions(userId: String? = null): List<PendingOfflineAction> {
        val actions = mutableListOf<PendingOfflineAction>()
        readableDatabase.query(
            "pending_actions",
            arrayOf("user_id", "action_type", "dedupe_key", "payload"),
            if (userId == null) null else "user_id = ?",
            if (userId == null) null else arrayOf(userId),
            null,
            null,
            "updated_at ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val type = runCatching { OfflineActionType.valueOf(cursor.getString(1)) }.getOrNull()
                    ?: continue
                actions += PendingOfflineAction(
                    userId = cursor.getString(0),
                    type = type,
                    dedupeKey = cursor.getString(2),
                    payload = cursor.getString(3)
                )
            }
        }
        return actions
    }

    @Synchronized
    fun delete(action: PendingOfflineAction) {
        writableDatabase.delete(
            "pending_actions",
            "user_id = ? AND action_type = ? AND dedupe_key = ?",
            arrayOf(action.userId, action.type.name, action.dedupeKey)
        )
    }

    @Synchronized
    fun pendingCount(): Int = readableDatabase.rawQuery(
        "SELECT COUNT(*) FROM pending_actions",
        null
    ).use { if (it.moveToFirst()) it.getInt(0) else 0 }
}
