package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
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
    savedIngredients: List<SavedIngredient>,
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
            val onIngredientChange = { changed: Ingredient ->
                val ingredients = recipe.ingredients.toMutableList()
                ingredients[index] = changed
                onRecipeChange(recipe.copy(ingredients = ingredients))
            }
            // The stepper keeps half-typed text, which must stay with its ingredient.
            key(ingredient.name) {
                val count = ingredient.count
                if (count == null) {
                    ValueStepper(
                        label = ingredient.name,
                        value = ingredient.percentage,
                        rules = PercentRules,
                        onValueChange = {
                            onIngredientChange(ingredient.copy(percentage = it))
                        }
                    )
                } else {
                    ValueStepper(
                        label = ingredient.name,
                        value = count.halvesFor(recipe.portionCount),
                        rules = CountRules,
                        onValueChange = {
                            val set = count.copy(halves = it, forPortions = recipe.portionCount)
                            onIngredientChange(ingredient.copy(count = set))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditRows(
    editor: IngredientListEditor,
    savedIngredients: List<SavedIngredient>,
    onEditorChange: (IngredientListEditor) -> Unit,
    modifier: Modifier = Modifier
) {
    // The drag library keeps the callback from when the list last changed.
    val currentEditor by rememberUpdatedState(editor)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HydrationEditRow()
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
                        gramsEachError = editor.gramsEachError(row.key),
                        offeredNames = editor.offeredNames(
                            row.key,
                            savedIngredients.map { it.name }
                        ),
                        onNameChange = { onEditorChange(editor.rename(row.key, it)) },
                        onNamePick = { name ->
                            savedIngredients.firstOrNull { it.name == name }?.let {
                                onEditorChange(editor.pick(row.key, it))
                            }
                        },
                        onIsCountChange = { onEditorChange(editor.setCount(row.key, it)) },
                        onGramsEachChange = {
                            onEditorChange(editor.changeGramsEach(row.key, it))
                        },
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

/** Hydration while editing, which can't be changed. */
@Composable
private fun HydrationEditRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.heightIn(min = ControlRowHeight),
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
}

/**
 * One ingredient while editing: a drag handle, its name, a remove button, and below them how it is
 * measured. [onMoveUp] and [onMoveDown] are null where the row can't move that way.
 */
@Composable
private fun EditRow(
    row: EditedIngredient,
    nameError: String?,
    gramsEachError: String?,
    offeredNames: List<String>,
    onNameChange: (String) -> Unit,
    onNamePick: (String) -> Unit,
    onIsCountChange: (Boolean) -> Unit,
    onGramsEachChange: (String) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    handleModifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EditNameRow(
            row = row,
            nameError = nameError,
            offeredNames = offeredNames,
            onNameChange = onNameChange,
            onNamePick = onNamePick,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onRemove = onRemove,
            handleModifier = handleModifier
        )
        CountChoice(otherLabel = "%", isCount = row.isCount, onIsCountChange = onIsCountChange)
        if (row.isCount) {
            GramsEachRow(
                displayName = row.displayName,
                gramsEach = row.gramsEach,
                error = gramsEachError,
                onGramsEachChange = onGramsEachChange
            )
        }
    }
}

/** The line of an edited ingredient with its drag handle, name and remove button. */
@Composable
private fun EditNameRow(
    row: EditedIngredient,
    nameError: String?,
    offeredNames: List<String>,
    onNameChange: (String) -> Unit,
    onNamePick: (String) -> Unit,
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
                onNamePick = onNamePick,
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
