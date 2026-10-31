package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.flow.drop

private const val NOTES_MIN_LINES = 3

/**
 * Edits a recipe's note, where typing `- ` or `1. ` starts a list. The field owns the text from
 * [initialNotes] on and only reports changes: to show another recipe's note, it has to start anew.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeNotesField(
    initialNotes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberSaveable(saver = RichTextState.Saver) {
        RichTextState().setHtml(initialNotes)
    }
    val currentOnNotesChange by rememberUpdatedState(onNotesChange)
    LaunchedEffect(state) {
        snapshotFlow {
            // The editor has no change callback, and this is what it updates on every change.
            val text = state.annotatedString.text
            if (text.isBlank()) "" else state.toHtml()
        }
            .drop(1)
            .collect { currentOnNotesChange(it) }
    }
    OutlinedRichTextEditor(
        state = state,
        label = { Text("Notes") },
        minLines = NOTES_MIN_LINES,
        modifier = modifier.fillMaxWidth()
    )
}

/** Shows a recipe's note with its lists formatted, or nothing without a note. */
@OptIn(ExperimentalRichTextApi::class)
@Composable
internal fun RecipeNotes(notes: String, modifier: Modifier = Modifier) {
    if (notes.isBlank()) return
    val state = remember(notes) { RichTextState().setHtml(notes) }
    RichText(state = state, modifier = modifier.fillMaxWidth())
}
