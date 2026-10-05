package com.example.pizza.dough

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

/**
 * Shows [message] with an Undo action, replacing any message already showing, and returns whether
 * Undo was tapped. Returns false once the message times out or is replaced.
 */
internal suspend fun SnackbarHostState.showUndoMessage(message: String): Boolean {
    // Messages queue up, so the previous one has to go before this one can show.
    currentSnackbarData?.dismiss()
    val result = showSnackbar(
        message = message,
        actionLabel = "Undo",
        // With an action, the message would otherwise stay until dismissed.
        duration = SnackbarDuration.Long
    )
    return result == SnackbarResult.ActionPerformed
}
