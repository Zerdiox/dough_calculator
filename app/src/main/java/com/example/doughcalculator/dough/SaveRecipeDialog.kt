package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/** The recipe's ingredient names that are not among [savedIngredients], ignoring case. */
internal fun DoughRecipe.namesNotIn(savedIngredients: List<String>): List<String> =
    ingredients.map { it.name }.filter { name ->
        savedIngredients.none { it.isSameNameAs(name) }
    }

/** Asks for a new recipe's name, with [newNamesChecklist] below the field. */
@Composable
internal fun SaveRecipeDialog(
    newNamesChecklist: @Composable () -> Unit,
    onSave: (name: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(name.trim())
            }, enabled = name.isNotBlank()) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Save recipe") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("e.g. Friday pizza night") },
                    singleLine = true
                )
                newNamesChecklist()
            }
        },
        modifier = modifier
    )
}

/**
 * Lets the user pick which of [newNames], ingredient names that aren't saved ingredients yet, to
 * add to the saved ingredients. Shows nothing when there are none.
 */
@Composable
internal fun NewNamesChecklist(
    newNames: List<String>,
    ticked: Set<String>,
    onTickedChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    if (newNames.isEmpty()) return
    Column(modifier = modifier.padding(top = 16.dp)) {
        Text(text = "Add to saved ingredients", style = MaterialTheme.typography.titleSmall)
        newNames.forEach { name ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = name in ticked,
                        role = Role.Checkbox,
                        onValueChange = { tick ->
                            onTickedChange(if (tick) ticked + name else ticked - name)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // The row handles the tap, so the whole line is the touch target.
                Checkbox(checked = name in ticked, onCheckedChange = null)
                Text(
                    text = name,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                )
            }
        }
    }
}
