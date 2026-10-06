package com.example.doughcalculator.dough

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.doughcalculator.R
import com.example.doughcalculator.ui.theme.DoughCalculatorTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoughCalculatorContent(
    recipe: DoughRecipe,
    loadedRecipeName: String?,
    ingredientEditor: IngredientListEditor?,
    savedIngredients: List<String>,
    resetMessagePending: Boolean,
    onRecipeChange: (DoughRecipe) -> Unit,
    onStartEditing: () -> Unit,
    onEditorChange: (IngredientListEditor) -> Unit,
    onFinishEditing: () -> Unit,
    onSaveRecipe: (name: String, namesToSave: Set<String>) -> Unit,
    onUpdateLoadedRecipe: (namesToSave: Set<String>) -> Unit,
    onOpenSavedRecipes: () -> Unit,
    onOpenFullRecipe: () -> Unit,
    onDismissResetMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    var saveStep by rememberSaveable { mutableStateOf(SaveStep.None) }
    // Held here, not in a dialog, so ticks carry from the update-or-new choice to the name.
    var namesToSave by rememberSaveable { mutableStateOf(emptySet<String>()) }
    EditModeEffects(
        editor = ingredientEditor,
        snackbarHostState = snackbarHostState,
        onEditorChange = onEditorChange,
        onFinishEditing = onFinishEditing
    )
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CalculatorTopBar(
                onSaveClick = {
                    // Each save starts unticked, whether the last one was saved or cancelled.
                    namesToSave = emptySet()
                    saveStep = if (loadedRecipeName == null) SaveStep.Name else SaveStep.Choice
                },
                onOpenSavedRecipes = onOpenSavedRecipes,
                actionsEnabled = ingredientEditor == null,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        CalculatorBody(
            recipe = recipe,
            ingredientEditor = ingredientEditor,
            savedIngredients = savedIngredients,
            onRecipeChange = onRecipeChange,
            onStartEditing = onStartEditing,
            onEditorChange = onEditorChange,
            onFinishEditing = onFinishEditing,
            onOpenFullRecipe = onOpenFullRecipe,
            // The Scaffold leaves the keyboard out of its padding, so the body makes room for it
            // and the focused field scrolls into view above it.
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
        )
        SaveDialogs(
            step = saveStep,
            loadedRecipeName = loadedRecipeName,
            newNames = recipe.namesNotIn(savedIngredients),
            namesToSave = namesToSave,
            snackbarHostState = snackbarHostState,
            onNamesToSaveChange = { namesToSave = it },
            onSaveRecipe = { onSaveRecipe(it, namesToSave) },
            onUpdateLoadedRecipe = { onUpdateLoadedRecipe(namesToSave) },
            onStepChange = { saveStep = it }
        )
        if (resetMessagePending) {
            ResetMessageDialog(onConfirm = onDismissResetMessage)
        }
    }
}

@Composable
private fun CalculatorBody(
    recipe: DoughRecipe,
    ingredientEditor: IngredientListEditor?,
    savedIngredients: List<String>,
    onRecipeChange: (DoughRecipe) -> Unit,
    onStartEditing: () -> Unit,
    onEditorChange: (IngredientListEditor) -> Unit,
    onFinishEditing: () -> Unit,
    onOpenFullRecipe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PortionsCard(recipe = recipe, onRecipeChange = onRecipeChange)
        IngredientsCard(
            recipe = recipe,
            editor = ingredientEditor,
            savedIngredients = savedIngredients,
            onRecipeChange = onRecipeChange,
            onStartEditing = onStartEditing,
            onEditorChange = onEditorChange,
            onFinishEditing = onFinishEditing
        )
        DoughResultCard(recipe = recipe)
        Button(
            onClick = onOpenFullRecipe,
            enabled = ingredientEditor == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = "Start kneading", style = MaterialTheme.typography.titleMedium)
        }
    }
}

/**
 * While editing, makes Back act as Done and shows "Removed" with Undo after each removal. A newer
 * removal or Done ends the previous message, because the effect restarts when the latest removal
 * changes.
 */
@Composable
private fun EditModeEffects(
    editor: IngredientListEditor?,
    snackbarHostState: SnackbarHostState,
    onEditorChange: (IngredientListEditor) -> Unit,
    onFinishEditing: () -> Unit
) {
    // Active for the whole edit, so Back can't leave the screen while a name can't be used.
    BackHandler(enabled = editor != null) {
        if (editor?.canApply == true) onFinishEditing()
    }
    val currentEditor by rememberUpdatedState(editor)
    val currentOnEditorChange by rememberUpdatedState(onEditorChange)
    val removal = editor?.lastRemoval
    LaunchedEffect(removal) {
        if (removal == null) return@LaunchedEffect
        val undo = snackbarHostState.showUndoMessage("Removed \"${removal.row.displayName}\"")
        // Once the message is gone, so is the removal, or rotating would show the message again.
        val editor = currentEditor?.takeIf { it.lastRemoval == removal } ?: return@LaunchedEffect
        currentOnEditorChange(if (undo) editor.undoRemove() else editor.clearRemoval())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorTopBar(
    onSaveClick: () -> Unit,
    onOpenSavedRecipes: () -> Unit,
    actionsEnabled: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Dough calculator") },
        modifier = modifier,
        actions = {
            IconButton(onClick = onSaveClick, enabled = actionsEnabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_bookmark_add),
                    contentDescription = "Save recipe"
                )
            }
            IconButton(onClick = onOpenSavedRecipes, enabled = actionsEnabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_bookmarks),
                    contentDescription = "Saved recipes"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

/** The recipe's ingredient names that are not among [savedIngredients], ignoring case. */
private fun DoughRecipe.namesNotIn(savedIngredients: List<String>): List<String> =
    ingredients.map { it.name }.filter { name ->
        savedIngredients.none { it.isSameNameAs(name) }
    }

/** Which save dialog shows: none, the choice to update the loaded recipe, or the name. */
private enum class SaveStep { None, Choice, Name }

@Composable
private fun SaveDialogs(
    step: SaveStep,
    loadedRecipeName: String?,
    newNames: List<String>,
    namesToSave: Set<String>,
    snackbarHostState: SnackbarHostState,
    onNamesToSaveChange: (Set<String>) -> Unit,
    onSaveRecipe: (name: String) -> Unit,
    onUpdateLoadedRecipe: () -> Unit,
    onStepChange: (SaveStep) -> Unit
) {
    val scope = rememberCoroutineScope()
    if (step == SaveStep.Choice && loadedRecipeName != null) {
        SaveChoiceDialog(
            loadedRecipeName = loadedRecipeName,
            newNamesChecklist = {
                NewNamesChecklist(newNames, namesToSave, onNamesToSaveChange)
            },
            onUpdate = {
                onUpdateLoadedRecipe()
                onStepChange(SaveStep.None)
                scope.launch { snackbarHostState.showSnackbar("Updated \"$loadedRecipeName\"") }
            },
            onSaveAsNew = { onStepChange(SaveStep.Name) },
            onDismiss = { onStepChange(SaveStep.None) }
        )
    }
    if (step == SaveStep.Name) {
        SaveRecipeDialog(
            newNamesChecklist = {
                NewNamesChecklist(newNames, namesToSave, onNamesToSaveChange)
            },
            onSave = { name ->
                onSaveRecipe(name)
                onStepChange(SaveStep.None)
                scope.launch { snackbarHostState.showSnackbar("Saved \"$name\"") }
            },
            onDismiss = { onStepChange(SaveStep.None) }
        )
    }
}

/** Asks whether to update the recipe the calculator was loaded from or to save a new one. */
@Composable
private fun SaveChoiceDialog(
    loadedRecipeName: String,
    newNamesChecklist: @Composable () -> Unit,
    onUpdate: () -> Unit,
    onSaveAsNew: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Save recipe") },
        text = {
            Column {
                TextButton(onClick = onUpdate) { Text("Update \"$loadedRecipeName\"") }
                TextButton(onClick = onSaveAsNew) { Text("Save as new…") }
                newNamesChecklist()
            }
        },
        modifier = modifier
    )
}

@Composable
private fun SaveRecipeDialog(
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
private fun NewNamesChecklist(
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

@Composable
private fun ResetMessageDialog(onConfirm: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        // Only OK clears the message; closing it any other way would bring it back next launch.
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK") }
        },
        title = { Text("Saved recipes couldn't be read") },
        text = {
            Text(
                "The app had to start fresh. A copy of your old data was kept on this phone " +
                    "so it can be recovered."
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun DoughCalculatorContentPreview() {
    DoughCalculatorTheme {
        DoughCalculatorContent(
            recipe = DefaultDoughRecipe,
            loadedRecipeName = null,
            ingredientEditor = null,
            savedIngredients = emptyList(),
            resetMessagePending = false,
            onRecipeChange = {},
            onStartEditing = {},
            onEditorChange = {},
            onFinishEditing = {},
            onSaveRecipe = { _, _ -> },
            onUpdateLoadedRecipe = {},
            onOpenSavedRecipes = {},
            onOpenFullRecipe = {},
            onDismissResetMessage = {}
        )
    }
}
