package com.swipehire.app.ui

import com.swipehire.app.data.AppLanguage
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
            "Application status"
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
}
