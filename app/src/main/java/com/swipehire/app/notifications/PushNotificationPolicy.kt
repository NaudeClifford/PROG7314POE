package com.swipehire.app.notifications

object PushNotificationPolicy {
    fun shouldDisplay(
        type: String,
        pushNotifications: Boolean,
        matchAlerts: Boolean,
        messageAlerts: Boolean
    ): Boolean {
        if (!pushNotifications) return false
        return if (type == "MESSAGE") messageAlerts else matchAlerts
    }
}
