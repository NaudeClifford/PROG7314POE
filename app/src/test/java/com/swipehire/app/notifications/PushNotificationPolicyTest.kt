package com.swipehire.app.notifications

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PushNotificationPolicyTest {
    @Test
    fun masterSwitchDisablesEveryNotification() {
        assertFalse(PushNotificationPolicy.shouldDisplay("MESSAGE", false, true, true))
        assertFalse(PushNotificationPolicy.shouldDisplay("MATCH", false, true, true))
    }

    @Test
    fun messageSwitchOnlyControlsMessages() {
        assertFalse(PushNotificationPolicy.shouldDisplay("MESSAGE", true, true, false))
        assertTrue(PushNotificationPolicy.shouldDisplay("MATCH", true, true, false))
    }

    @Test
    fun matchSwitchControlsMatchInterviewAndStageAlerts() {
        assertFalse(PushNotificationPolicy.shouldDisplay("MATCH", true, false, true))
        assertFalse(PushNotificationPolicy.shouldDisplay("INTERVIEW", true, false, true))
        assertFalse(PushNotificationPolicy.shouldDisplay("STAGE", true, false, true))
    }
}
