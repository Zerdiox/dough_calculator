package com.example.doughcalculator.dough

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.doughcalculator.R
import com.example.doughcalculator.ui.theme.DoughCalculatorTheme
import kotlinx.coroutines.launch

/**
 * Turns a recipe typed in grams into baker's percentages. [onSaveRecipe] saves it as a new recipe;
 * afterwards [onOpenSavedRecipe] opens it, unless "Add more" asks to stay for the next recipe.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertRecipeScreen(
    draft: ConversionDraft,
    savedIngredients: List<String>,
    onDraftChange: (ConversionDraft) -> Unit,
    onUseInCalculator: (DoughRecipe) -> Unit,
    onSaveRecipe: (name: String, recipe: DoughRecipe, namesToSave: Set<String>) -> Unit,
    onOpenSavedRecipe: (name: String, recipe: DoughRecipe) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var saving by rememberSaveable { mutableStateOf(false) }
    var namesToSave by rememberSaveable { mutableStateOf(emptySet<String>()) }
    val recipe = draft.toRecipe()
    RemovalUndo(draft = draft, snackbarHostState = snackbarHostState, onDraftChange = onDraftChange)
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Convert recipe") },
                navigationIcon = { BackButton(onClick = onBack) },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        ConvertBody(
            draft = draft,
            preview = draft.preview(),
            canConvert = recipe != null,
            savedIngredients = savedIngredients,
            onDraftChange = onDraftChange,
            onSave = {
                // Each save starts unticked, whether the last one was saved or cancelled.
                namesToSave = emptySet()
                saving = true
            },
            onUseInCalculator = { recipe?.let(onUseInCalculator) },
            // The Scaffold leaves the keyboard out of its padding, so the body makes room for it.
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
        )
        if (saving && recipe != null) {
            SaveRecipeDialog(
                newNamesChecklist = {
                    NewNamesChecklist(
                        newNames = recipe.namesNotIn(savedIngredients),
                        ticked = namesToSave,
                        onTickedChange = { namesToSave = it }
                    )
                },
                onSave = { name ->
                    saving = false
                    onSaveRecipe(name, recipe, namesToSave)
                    if (!draft.addMore) {
                        onOpenSavedRecipe(name, recipe)
                        return@SaveRecipeDialog
                    }
                    onDraftChange(draft.clearedForNext())
                    scope.launch { snackbarHostState.showSnackbar("Saved \"$name\"") }
                },
                onDismiss = { saving = false }
            )
        }
    }
}

@Composable
private fun ConvertBody(
    draft: ConversionDraft,
    preview: DoughRecipe?,
    canConvert: Boolean,
    savedIngredients: List<String>,
    onDraftChange: (ConversionDraft) -> Unit,
    onSave: () -> Unit,
    onUseInCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WeightsCard(
            draft = draft,
            savedIngredients = savedIngredients,
            onDraftChange = onDraftChange
        )
        PortionCountCard(draft = draft, onDraftChange = onDraftChange)
        if (preview != null) DoughResultCard(recipe = preview) else FlourNeededCard()
        ConvertActions(
            draft = draft,
            enabled = canConvert,
            onDraftChange = onDraftChange,
            onSave = onSave,
            onUseInCalculator = onUseInCalculator
        )
    }
}

/**
 * Shows "Removed" with Undo after each removed row. A newer removal or clearing the draft ends the
 * previous message, because the effect restarts when the latest removal changes.
 */
@Composable
private fun RemovalUndo(
    draft: ConversionDraft,
    snackbarHostState: SnackbarHostState,
    onDraftChange: (ConversionDraft) -> Unit
) {
    val currentDraft by rememberUpdatedState(draft)
    val currentOnDraftChange by rememberUpdatedState(onDraftChange)
    val removal = draft.rows.lastRemoval
    LaunchedEffect(removal) {
        if (removal == null) return@LaunchedEffect
        val undo = snackbarHostState.showUndoMessage("Removed \"${removal.row.displayName}\"")
        // Once the message is gone, so is the removal, or rotating would show the message again.
        val draft = currentDraft.takeIf { it.rows.lastRemoval == removal } ?: return@LaunchedEffect
        val rows = if (undo) draft.rows.undoRemove() else draft.rows.clearRemoval()
        currentOnDraftChange(draft.copy(rows = rows))
    }
}

/** The flour, the water and the other ingredients, each with its weight in grams. */
@Composable
private fun WeightsCard(
    draft: ConversionDraft,
    savedIngredients: List<String>,
    onDraftChange: (ConversionDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title = "Ingredients", subtitle = "As in the recipe, in grams")
            FixedWeightRow(
                name = "Flour",
                grams = draft.flour,
                error = draft.flourError,
                onGramsChange = { onDraftChange(draft.copy(flour = it)) }
            )
            FixedWeightRow(
                name = "Water",
                grams = draft.water,
                error = draft.waterError,
                onGramsChange = { onDraftChange(draft.copy(water = it)) }
            )
            val rows = draft.rows
            rows.items.forEach { row ->
                // Fields keep their focus and dropdown, which must stay with their row.
                key(row.key) {
                    IngredientWeightRow(
                        row = row,
                        nameError = rows.nameError(row.key),
                        gramsError = draft.rowGramsError(row.key),
                        offeredNames = rows.offeredNames(row.key, savedIngredients),
                        onNameChange = {
                            onDraftChange(draft.copy(rows = rows.rename(row.key, it)))
                        },
                        onGramsChange = {
                            onDraftChange(draft.copy(rows = rows.changeGrams(row.key, it)))
                        },
                        onRemove = { onDraftChange(draft.copy(rows = rows.remove(row.key))) }
                    )
                }
            }
            TextButton(onClick = { onDraftChange(draft.copy(rows = rows.add())) }) {
                Icon(painter = painterResource(R.drawable.ic_add), contentDescription = null)
                Text(text = "Add ingredient", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

/** Flour or water: a fixed name and its weight. */
@Composable
private fun FixedWeightRow(
    name: String,
    grams: String,
    error: String?,
    onGramsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = ControlRowHeight)
                    .wrapContentHeight(Alignment.CenterVertically)
            )
            GramsField(
                grams = grams,
                description = name,
                isError = error != null,
                onGramsChange = onGramsChange
            )
        }
        FieldError(error)
    }
}

/** Another ingredient: its name with the saved names to pick from, its weight and remove. */
@Composable
private fun IngredientWeightRow(
    row: ConvertedRow,
    nameError: String?,
    gramsError: String?,
    offeredNames: List<String>,
    onNameChange: (String) -> Unit,
    onGramsChange: (String) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Top) {
            NameFieldWithSavedNames(
                name = row.name,
                error = nameError,
                offeredNames = offeredNames,
                onNameChange = onNameChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = CompactFieldInset)
            )
            GramsField(
                grams = row.grams,
                description = "Grams of ${row.displayName}",
                isError = gramsError != null,
                onGramsChange = onGramsChange,
                modifier = Modifier.padding(start = 8.dp, top = CompactFieldInset)
            )
            IconButton(onClick = onRemove, modifier = FieldHeight) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Remove ${row.displayName}"
                )
            }
        }
        FieldError(gramsError)
    }
}

/** A weight typed in grams, with "g" after it and a keyboard for decimals. */
@Composable
private fun GramsField(
    grams: String,
    description: String,
    isError: Boolean,
    onGramsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    CompactTextField(
        value = grams,
        onValueChange = onGramsChange,
        // The row's name is a separate text, so the field carries it for screen readers.
        modifier = modifier
            .width(FieldWidth)
            .semantics { contentDescription = description },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        suffix = { Text("g") },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true
    )
}

/** Why a weight can't be used, under its row, where a long message has room. */
@Composable
private fun FieldError(error: String?, modifier: Modifier = Modifier) {
    if (error.isNullOrEmpty()) return
    Text(
        text = error,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.End,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun PortionCountCard(
    draft: ConversionDraft,
    onDraftChange: (ConversionDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionHeader(
                    title = "Portions",
                    subtitle = "How many to divide it into",
                    modifier = Modifier.weight(1f)
                )
                PortionStepper(
                    count = draft.portionCount,
                    onCountChange = { onDraftChange(draft.copy(portionCount = it)) }
                )
            }
            FieldError(draft.portionsError)
        }
    }
}

/** "Add more", and the buttons that save the recipe or put it on the calculator. */
@Composable
private fun ConvertActions(
    draft: ConversionDraft,
    enabled: Boolean,
    onDraftChange: (ConversionDraft) -> Unit,
    onSave: () -> Unit,
    onUseInCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = draft.addMore,
                    role = Role.Checkbox,
                    onValueChange = { onDraftChange(draft.copy(addMore = it)) }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // The row handles the tap, so the whole line is the touch target.
            Checkbox(checked = draft.addMore, onCheckedChange = null)
            Text(
                text = "Add more",
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onSave,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("Save as recipe")
            }
            Button(
                onClick = onUseInCalculator,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("Use in calculator")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConvertRecipeScreenPreview() {
    DoughCalculatorTheme {
        ConvertRecipeScreen(
            draft = ConversionDraft(portionCount = 4, flour = "500", water = "325"),
            savedIngredients = emptyList(),
            onDraftChange = {},
            onUseInCalculator = {},
            onSaveRecipe = { _, _, _ -> },
            onOpenSavedRecipe = { _, _ -> },
            onBack = {}
        )
    }
}
