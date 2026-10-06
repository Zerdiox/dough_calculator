package com.example.doughcalculator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.doughcalculator.dough.ConvertRecipeScreen
import com.example.doughcalculator.dough.DoughCalculatorContent
import com.example.doughcalculator.dough.DoughRecipe
import com.example.doughcalculator.dough.DoughViewModel
import com.example.doughcalculator.dough.FullRecipeScreen
import com.example.doughcalculator.dough.SavedIngredientsScreen
import com.example.doughcalculator.dough.SavedRecipesScreen
import kotlinx.serialization.Serializable

@Serializable
private data object CalculatorKey : NavKey

@Serializable
private data object SavedRecipesKey : NavKey

@Serializable
private data object SavedIngredientsKey : NavKey

@Serializable
private data object ConvertKey : NavKey

@Serializable
private data class FullRecipeKey(val title: String, val recipe: DoughRecipe) : NavKey

/** The app's screens and the navigation between them. */
@Composable
fun DoughCalculatorApp(
    modifier: Modifier = Modifier,
    viewModel: DoughViewModel = viewModel(factory = DoughViewModel.Factory)
) {
    val savedRecipes by viewModel.savedRecipes.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(CalculatorKey)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            calculatorEntry(
                viewModel = viewModel,
                onOpenSavedRecipes = { backStack.add(SavedRecipesKey) },
                onOpenConvert = { portionCount ->
                    // Started here rather than on the screen, so rotating keeps what was typed.
                    viewModel.conversion.start(portionCount)
                    backStack.add(ConvertKey)
                },
                onOpenFullRecipe = {
                    backStack.add(FullRecipeKey(title = "Full recipe", recipe = it))
                }
            )
            entry<SavedRecipesKey> {
                val recipes = savedRecipes ?: return@entry
                SavedRecipesScreen(
                    savedRecipes = recipes,
                    onOpenRecipe = {
                        backStack.add(FullRecipeKey(title = it.name, recipe = it.recipe))
                    },
                    onEditRecipe = {
                        viewModel.loadRecipe(it)
                        backStack.removeLastOrNull()
                    },
                    onDeleteRecipe = { viewModel.deleteRecipe(it.id) },
                    onRestoreRecipe = viewModel::restoreRecipe,
                    onOpenSavedIngredients = { backStack.add(SavedIngredientsKey) },
                    onBack = { backStack.removeLastOrNull() }
                )
            }
            savedIngredientsEntry(viewModel = viewModel, onBack = { backStack.removeLastOrNull() })
            convertEntry(
                viewModel = viewModel,
                onBack = { backStack.removeLastOrNull() },
                onOpenSavedRecipe = { name, recipe ->
                    // In place of Convert, so Back from the recipe returns to the calculator.
                    backStack.removeLastOrNull()
                    backStack.add(FullRecipeKey(title = name, recipe = recipe))
                }
            )
            entry<FullRecipeKey> { key ->
                FullRecipeScreen(
                    title = key.title,
                    initialRecipe = key.recipe,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}

/** The calculator, collecting its state only while it is shown. */
private fun EntryProviderScope<NavKey>.calculatorEntry(
    viewModel: DoughViewModel,
    onOpenSavedRecipes: () -> Unit,
    onOpenConvert: (portionCount: Int) -> Unit,
    onOpenFullRecipe: (DoughRecipe) -> Unit
) {
    entry<CalculatorKey> {
        val recipe by viewModel.recipe.collectAsStateWithLifecycle()
        val loadedRecipe by viewModel.loadedRecipe.collectAsStateWithLifecycle()
        val ingredientEditor by viewModel.ingredientEditing.editor.collectAsStateWithLifecycle()
        val savedIngredients by viewModel.savedIngredients.collectAsStateWithLifecycle()
        val resetMessagePending by viewModel.resetMessagePending.collectAsStateWithLifecycle()
        val currentRecipe = recipe ?: return@entry
        DoughCalculatorContent(
            recipe = currentRecipe,
            loadedRecipeName = loadedRecipe?.name,
            ingredientEditor = ingredientEditor,
            savedIngredients = savedIngredients.orEmpty(),
            resetMessagePending = resetMessagePending,
            onRecipeChange = viewModel::updateRecipe,
            onStartEditing = { viewModel.ingredientEditing.start(currentRecipe.ingredients) },
            onEditorChange = viewModel.ingredientEditing::change,
            onFinishEditing = { viewModel.ingredientEditing.finish(currentRecipe) },
            onSaveRecipe = { name, namesToSave ->
                viewModel.saveRecipe(name, currentRecipe, namesToSave)
            },
            onUpdateLoadedRecipe = { namesToSave ->
                loadedRecipe?.let { viewModel.updateSavedRecipe(it.id, currentRecipe, namesToSave) }
            },
            onOpenSavedRecipes = onOpenSavedRecipes,
            onOpenConvert = { onOpenConvert(currentRecipe.portionCount) },
            onOpenFullRecipe = { onOpenFullRecipe(currentRecipe) },
            onDismissResetMessage = viewModel::dismissResetMessage
        )
    }
}

/** The saved ingredients, collecting them only while they are shown. */
private fun EntryProviderScope<NavKey>.savedIngredientsEntry(
    viewModel: DoughViewModel,
    onBack: () -> Unit
) {
    entry<SavedIngredientsKey> {
        val savedIngredients by viewModel.savedIngredients.collectAsStateWithLifecycle()
        SavedIngredientsScreen(
            savedIngredients = savedIngredients ?: return@entry,
            onDeleteIngredient = viewModel::deleteSavedIngredient,
            onRestoreIngredient = viewModel::restoreSavedIngredient,
            onBack = onBack
        )
    }
}

/** The Convert screen, collecting its draft and the saved ingredients only while it is shown. */
private fun EntryProviderScope<NavKey>.convertEntry(
    viewModel: DoughViewModel,
    onBack: () -> Unit,
    onOpenSavedRecipe: (name: String, recipe: DoughRecipe) -> Unit
) {
    entry<ConvertKey> {
        val draft by viewModel.conversion.draft.collectAsStateWithLifecycle()
        val savedIngredients by viewModel.savedIngredients.collectAsStateWithLifecycle()
        ConvertRecipeScreen(
            draft = draft,
            savedIngredients = savedIngredients.orEmpty(),
            onDraftChange = viewModel.conversion::change,
            onUseInCalculator = {
                viewModel.useRecipe(it)
                onBack()
            },
            onSaveRecipe = { name, recipe, namesToSave ->
                viewModel.saveRecipe(name, recipe, namesToSave, linkCalculator = false)
            },
            onOpenSavedRecipe = onOpenSavedRecipe,
            onBack = onBack
        )
    }
}
