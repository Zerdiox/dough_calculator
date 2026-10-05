package com.example.pizza

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pizza.dough.DoughCalculatorContent
import com.example.pizza.dough.DoughRecipe
import com.example.pizza.dough.DoughViewModel
import com.example.pizza.dough.FullRecipeScreen
import com.example.pizza.dough.SavedRecipesScreen
import kotlinx.serialization.Serializable

@Serializable
private data object CalculatorKey : NavKey

@Serializable
private data object SavedRecipesKey : NavKey

@Serializable
private data class FullRecipeKey(val title: String, val recipe: DoughRecipe) : NavKey

/** The app's screens and the navigation between them. */
@Composable
fun PizzaApp(
    modifier: Modifier = Modifier,
    viewModel: DoughViewModel = viewModel(factory = DoughViewModel.Factory)
) {
    val recipe by viewModel.recipe.collectAsStateWithLifecycle()
    val savedRecipes by viewModel.savedRecipes.collectAsStateWithLifecycle()
    val loadedRecipe by viewModel.loadedRecipe.collectAsStateWithLifecycle()
    val resetMessagePending by viewModel.resetMessagePending.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(CalculatorKey)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            entry<CalculatorKey> {
                val currentRecipe = recipe ?: return@entry
                DoughCalculatorContent(
                    recipe = currentRecipe,
                    loadedRecipeName = loadedRecipe?.name,
                    resetMessagePending = resetMessagePending,
                    onRecipeChange = viewModel::updateRecipe,
                    onSaveRecipe = { name -> viewModel.saveRecipe(name, currentRecipe) },
                    onUpdateLoadedRecipe = {
                        loadedRecipe?.let { viewModel.updateSavedRecipe(it.id, currentRecipe) }
                    },
                    onOpenSavedRecipes = { backStack.add(SavedRecipesKey) },
                    onOpenFullRecipe = {
                        backStack.add(FullRecipeKey(title = "Full recipe", recipe = currentRecipe))
                    },
                    onDismissResetMessage = viewModel::dismissResetMessage
                )
            }
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
                    onBack = { backStack.removeLastOrNull() }
                )
            }
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
