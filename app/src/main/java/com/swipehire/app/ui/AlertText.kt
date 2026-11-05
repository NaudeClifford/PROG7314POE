package com.swipehire.app.ui

import com.swipehire.app.data.AlertType
import com.swipehire.app.data.AppLanguage

data class LocalizedAlertText(val title: String, val body: String)

/** Translate the fixed parts of an alert while preserving names and user-written messages. */
fun localizeAlertText(type: AlertType, title: String, body: String, language: AppLanguage): LocalizedAlertText {
    val localizedTitle = when {
        type == AlertType.MATCH -> translate("New match", language)
        type == AlertType.MESSAGE && title.startsWith("New message from ") ->
            "${translate("New message from", language)} ${title.removePrefix("New message from ")}"
        type == AlertType.INTERVIEW || type == AlertType.STAGE -> translate(title, language)
        else -> title
    }
    val localizedBody = when {
        type == AlertType.MATCH && body.startsWith("You matched with ") ->
            "${translate("You matched with", language)} ${body.removePrefix("You matched with ")}"
        type == AlertType.STAGE -> "${translate("Now at stage", language)}: ${translate(body, language)}"
        type == AlertType.INTERVIEW && body.contains(" · ") -> {
            val slot = body.substringBeforeLast(" · ")
            val mode = body.substringAfterLast(" · ")
            "$slot · ${translate(mode, language)}"
        }
        else -> body
    }
    return LocalizedAlertText(localizedTitle, localizedBody)
}
