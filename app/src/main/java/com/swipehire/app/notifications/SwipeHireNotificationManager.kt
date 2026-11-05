package com.swipehire.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.swipehire.app.MainActivity
import com.swipehire.app.R
import com.swipehire.app.data.AlertType
import com.swipehire.app.data.AppLanguage
import com.swipehire.app.ui.LocalizedAlertText
import com.swipehire.app.ui.localizeAlertText
import com.swipehire.app.ui.translate

object SwipeHireNotificationManager {
    const val CHANNEL_ID = "swipehire_alerts"

    fun createChannel(context: Context, language: AppLanguage = AppLanguage.ENGLISH) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            translate(context.getString(R.string.notification_channel_name), language),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = translate(context.getString(R.string.notification_channel_description), language)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun show(context: Context, data: Map<String, String>, language: AppLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        createChannel(context, language)
        val alertType = AlertType.entries.firstOrNull { it.name == data["type"]?.uppercase() }
        val text = alertType?.let {
            localizeAlertText(it, data["title"].orEmpty(), data["body"].orEmpty(), language)
        } ?: LocalizedAlertText(data["title"].orEmpty(), data["body"].orEmpty())
        val notificationId = data["notificationId"].orEmpty()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notificationId", notificationId)
            putExtra("matchId", data["matchId"].orEmpty())
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(text.title.ifBlank { context.getString(R.string.app_name) })
            .setContentText(text.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId.hashCode(), notification)
    }
}
