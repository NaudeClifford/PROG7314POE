package com.swipehire.app.ui

import com.swipehire.app.data.AppLanguage
import com.swipehire.app.data.AlertType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LocalizationTest {
    @Test
    fun `core and new feature labels are translated in every supported local language`() {
        val keys = listOf(
            "Settings",
            "Biometric lock",
            "Offline synchronisation",
            "Schedule interview",
            "Application status",
            "Full name",
            "Hiring for (comma separated)",
            "Remove from saved",
            "Complete profiles are prioritised in recommendations.",
            "SwipeHire needs your location to sort listings by distance and show them on the map.",
            "Back to profile",
            "No Google account is available. Add one on your device or sign in with email below.",
            "change waiting to synchronise",
            "changes waiting to synchronise"
        )
        val localLanguages = listOf(AppLanguage.ISIZULU, AppLanguage.AFRIKAANS, AppLanguage.SESOTHO)

        localLanguages.forEach { language ->
            keys.forEach { key ->
                assertNotEquals("Missing $language translation for $key", key, translate(key, language))
            }
        }
    }

    @Test
    fun `unknown dynamic content safely falls back to English`() {
        val text = "A company name supplied by a user"
        assertEquals(text, translate(text, AppLanguage.ISIZULU))
    }

    @Test
    fun `alert text translates fixed phrases and preserves names and messages`() {
        assertEquals(
            LocalizedAlertText("Ukuhambisana okusha", "Uhambisane no Lerato."),
            localizeAlertText(AlertType.MATCH, "New match", "You matched with Lerato.", AppLanguage.ISIZULU)
        )
        assertEquals(
            LocalizedAlertText("Nuwe boodskap van Lerato", "Hello"),
            localizeAlertText(AlertType.MESSAGE, "New message from Lerato", "Hello", AppLanguage.AFRIKAANS)
        )
        assertEquals(
            LocalizedAlertText("Boemo ba kopo bo ntjhafaditswe", "Jwale ho mokgahlelo: Poledisano"),
            localizeAlertText(AlertType.STAGE, "Application status updated", "Interview", AppLanguage.SESOTHO)
        )
    }
}
