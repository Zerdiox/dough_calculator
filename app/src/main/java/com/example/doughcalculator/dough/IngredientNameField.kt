package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization

/** A name field offering the saved names while it has focus. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NameFieldWithSavedNames(
    name: String,
    error: String?,
    offeredNames: List<String>,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        // A tap on the field toggles; while it has focus, a tap should only open the list.
        onExpandedChange = { expanded = it || focused },
        modifier = modifier
    ) {
        NameField(
            name = name,
            error = error,
            onNameChange = {
                onNameChange(it)
                expanded = true
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onFocusChanged {
                    focused = it.isFocused
                    expanded = it.isFocused
                }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (offeredNames.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No saved ingredient matches") },
                    onClick = {},
                    enabled = false
                )
            }
            offeredNames.forEach { offered ->
                DropdownMenuItem(
                    text = { Text(offered) },
                    onClick = {
                        onNameChange(offered)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

/** An ingredient name, at most [MAX_NAME_LENGTH] characters, with the reason it can't be used. */
@Composable
internal fun NameField(
    name: String,
    error: String?,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    // A blank name is refused without a message; it's plainly unfinished.
    val message = error?.takeIf { it.isNotEmpty() }
    CompactTextField(
        value = name,
        onValueChange = { if (it.length <= MAX_NAME_LENGTH) onNameChange(it) },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Name") },
        supportingText = message?.let { { Text(it) } },
        isError = message != null,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true
    )
}
