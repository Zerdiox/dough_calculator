package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.doughcalculator.R
import sh.calvin.reorderable.ReorderableColumn

/**
 * Hydration and the recipe's other ingredients as percentages of the flour. Edit turns the
 * ingredients into a list to rename, reorder, remove and add to; Done applies it.
 *
 * [editor] is the draft while editing, or null otherwise.
 */
@Composable
internal fun IngredientsCard(
    recipe: DoughRecipe,
    editor: IngredientListEditor?,
    savedIngredients: List<String>,
    onRecipeChange: (DoughRecipe) -> Unit,
    onStartEditing: () -> Unit,
    onEditorChange: (IngredientListEditor) -> Unit,
    onFinishEditing: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    title = "Ingredients",
                    subtitle = "As a percentage of the flour weight",
                    modifier = Modifier.weight(1f)
                )
                if (editor == null) {
                    TextButton(onClick = onStartEditing) { Text("Edit") }
                } else {
                    TextButton(onClick = onFinishEditing, enabled = editor.canApply) {
                        Text("Done")
                    }
                }
            }
            if (editor == null) {
                PercentageRows(recipe = recipe, onRecipeChange = onRecipeChange)
            } else {
                EditRows(
                    editor = editor,
                    savedIngredients = savedIngredients,
                    onEditorChange = onEditorChange
                )
            }
        }
    }
}

@Composable
private fun PercentageRows(
    recipe: DoughRecipe,
    onRecipeChange: (DoughRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ValueStepper(
            label = "Hydration",
            value = recipe.hydration,
            rules = PercentRules,
            onValueChange = { onRecipeChange(recipe.copy(hydration = it)) }
        )
        recipe.ingredients.forEachIndexed { index, ingredient ->
            // The stepper keeps half-typed text, which must stay with its ingredient.
            key(ingredient.name) {
                ValueStepper(
                    label = ingredient.name,
                    value = ingredient.percentage,
                    rules = PercentRules,
                    onValueChange = { percentage ->
                        val ingredients = recipe.ingredients.toMutableList()
                        ingredients[index] = ingredient.copy(percentage = percentage)
                        onRecipeChange(recipe.copy(ingredients = ingredients))
                    }
                )
            }
        }
    }
}

@Composable
private fun EditRows(
    editor: IngredientListEditor,
    savedIngredients: List<String>,
    onEditorChange: (IngredientListEditor) -> Unit,
    modifier: Modifier = Modifier
) {
    // The drag library keeps the callback from when the list last changed.
    val currentEditor by rememberUpdatedState(editor)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.heightIn(min = ControlRowHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hydration",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "In every recipe",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ReorderableColumn(
            list = editor.rows,
            onSettle = { from, to -> onEditorChange(currentEditor.move(from, to)) },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { index, row, _ ->
            key(row.key) {
                ReorderableItem {
                    EditRow(
                        row = row,
                        nameError = editor.nameError(row.key),
                        offeredNames = editor.offeredNames(row.key, savedIngredients),
                        onNameChange = { onEditorChange(editor.rename(row.key, it)) },
                        onMoveUp = if (index > 0) {
                            { onEditorChange(editor.move(index, index - 1)) }
                        } else {
                            null
                        },
                        onMoveDown = if (index < editor.rows.lastIndex) {
                            { onEditorChange(editor.move(index, index + 1)) }
                        } else {
                            null
                        },
                        onRemove = { onEditorChange(editor.remove(row.key)) },
                        handleModifier = Modifier.draggableHandle()
                    )
                }
            }
        }
        TextButton(onClick = { onEditorChange(editor.add()) }) {
            Icon(painter = painterResource(R.drawable.ic_add), contentDescription = null)
            Text(text = "Add ingredient", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

/**
 * One ingredient while editing: a drag handle, its name and a remove button. [onMoveUp] and
 * [onMoveDown] are null where the row can't move that way.
 */
@Composable
private fun EditRow(
    row: EditedIngredient,
    nameError: String?,
    offeredNames: List<String>,
    onNameChange: (String) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    handleModifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        // Dragging needs a finger, so screen readers get the same moves as actions.
        Icon(
            painter = painterResource(R.drawable.ic_drag_handle),
            contentDescription = null,
            modifier = handleModifier
                .then(FieldHeight)
                .padding(horizontal = 12.dp)
                .semantics {
                    contentDescription = "Reorder ${row.displayName}"
                    customActions = listOfNotNull(
                        onMoveUp?.let {
                            CustomAccessibilityAction("Move up") {
                                it()
                                true
                            }
                        },
                        onMoveDown?.let {
                            CustomAccessibilityAction("Move down") {
                                it()
                                true
                            }
                        }
                    )
                }
        )
        if (row.isAdded) {
            NameFieldWithSavedNames(
                name = row.name,
                error = nameError,
                offeredNames = offeredNames,
                onNameChange = onNameChange,
                modifier = Modifier.weight(1f).padding(vertical = CompactFieldInset)
            )
        } else {
            NameField(
                name = row.name,
                error = nameError,
                onNameChange = onNameChange,
                modifier = Modifier.weight(1f).padding(vertical = CompactFieldInset)
            )
        }
        IconButton(onClick = onRemove, modifier = FieldHeight) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Remove ${row.displayName}"
            )
        }
    }
}

/** A name field for a row added in this edit, offering the saved names while it has focus. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NameFieldWithSavedNames(
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
private fun NameField(
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

/** Centres an icon on the name field, so it stays level when a message shows below. */
private val FieldHeight = Modifier
    .heightIn(min = ControlRowHeight)
    .wrapContentHeight(Alignment.CenterVertically)
