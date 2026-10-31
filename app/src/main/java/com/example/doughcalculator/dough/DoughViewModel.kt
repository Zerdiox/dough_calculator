package com.example.doughcalculator.dough

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.doughcalculator.DoughCalculatorApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The recipe on the calculator. [revision] goes up each time the recipe is replaced as a whole, and
 * travels with it so the screen can't start anew from an earlier recipe.
 */
data class CalculatorRecipe(val recipe: DoughRecipe, val revision: Int)

private fun CalculatorRecipe?.replacedBy(recipe: DoughRecipe) =
    CalculatorRecipe(recipe, (this?.revision ?: 0) + 1)

/**
 * Holds the calculator and saved recipes for the screens. Writes run in [writeScope], which
 * outlives this ViewModel, so a write still finishes when the screen that started it closes.
 */
class DoughViewModel(
    private val repository: DoughRepository,
    private val writeScope: CoroutineScope
) : ViewModel() {
    private val editedCalculator = MutableStateFlow<CalculatorRecipe?>(null)
    private var calculatorWrite: Job? = null

    /** The recipe on the calculator, or `null` while it is still loading. */
    val calculator: StateFlow<CalculatorRecipe?> =
        combine(repository.data, editedCalculator) { data, edited ->
            edited ?: CalculatorRecipe(data.currentRecipe, revision = 0)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** The saved recipes, or `null` while they are still loading. */
    val savedRecipes: StateFlow<List<SavedRecipe>?> = repository.data
        .map { it.savedRecipes }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** The saved recipe the calculator was loaded from, or `null` if none or it was deleted. */
    val loadedRecipe: StateFlow<SavedRecipe?> = repository.data
        .map { it.loadedRecipe }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** The saved ingredients, sorted ignoring case, or `null` while they are still loading. */
    val savedIngredients: StateFlow<List<SavedIngredient>?> = repository.savedIngredients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /**
     * The calculator's ingredient list while it is edited. Kept here so it survives rotation; it
     * is not stored, so closing the app drops it.
     */
    val ingredientEditing = IngredientEditing(onFinish = ::updateRecipe)

    /**
     * The recipe typed on the Convert screen. Kept here so it survives rotation; it is not stored,
     * so closing the app drops it.
     */
    val conversion = RecipeConverting()

    /** Whether the user still has to be told that unreadable data was set aside. */
    val resetMessagePending: StateFlow<Boolean> = repository.data
        .map { it.resetMessagePending }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), false)

    fun updateRecipe(recipe: DoughRecipe) {
        editedCalculator.update { CalculatorRecipe(recipe, it?.revision ?: 0) }
        // Saving waits for a short pause, so typing or stepping doesn't write on every change.
        writeCalculator {
            delay(SAVE_DEBOUNCE_MILLIS)
            repository.saveCurrentRecipe(recipe)
        }
    }

    fun loadRecipe(savedRecipe: SavedRecipe) {
        editedCalculator.update { it.replacedBy(savedRecipe.recipe) }
        // Not cancellable: a change made right after must not cut off the link to the recipe.
        writeCalculator { withContext(NonCancellable) { repository.loadRecipe(savedRecipe) } }
    }

    /** Puts [recipe] on the calculator, which then counts as not loaded from any saved recipe. */
    fun useRecipe(recipe: DoughRecipe) {
        editedCalculator.update { it.replacedBy(recipe) }
        // Not cancellable: a change made right after must not leave the old link in place.
        writeCalculator { withContext(NonCancellable) { repository.useRecipe(recipe) } }
    }

    /**
     * Saves [recipe] as new, adding [namesToSave] to the saved ingredients. With [linkCalculator],
     * the calculator counts as loaded from it; a recipe saved from Convert leaves the link alone.
     */
    fun saveRecipe(
        name: String,
        recipe: DoughRecipe,
        namesToSave: Set<String>,
        linkCalculator: Boolean = true
    ) {
        writeScope.launch { repository.saveRecipe(name, recipe, namesToSave, linkCalculator) }
    }

    /** Updates the saved recipe [id], adding [namesToSave] to the saved ingredients. */
    fun updateSavedRecipe(id: String, recipe: DoughRecipe, namesToSave: Set<String>) {
        writeScope.launch { repository.updateSavedRecipe(id, recipe, namesToSave) }
    }

    fun deleteSavedIngredient(name: String) {
        writeScope.launch { repository.deleteSavedIngredient(name) }
    }

    fun restoreSavedIngredient(ingredient: SavedIngredient) {
        writeScope.launch { repository.restoreSavedIngredient(ingredient) }
    }

    fun dismissResetMessage() {
        writeScope.launch { repository.dismissResetMessage() }
    }

    fun deleteRecipe(id: String) {
        writeScope.launch { repository.deleteRecipe(id) }
    }

    fun restoreRecipe(savedRecipe: SavedRecipe, index: Int) {
        writeScope.launch { repository.restoreRecipe(savedRecipe, index) }
    }

    /**
     * Replaces the pending calculator write with [write]. The pending one is cancelled and awaited
     * first, so it can never land after the newer one.
     */
    private fun writeCalculator(write: suspend () -> Unit) {
        val pending = calculatorWrite
        calculatorWrite = writeScope.launch {
            pending?.cancelAndJoin()
            write()
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val SAVE_DEBOUNCE_MILLIS = 300L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                        as DoughCalculatorApplication
                DoughViewModel(
                    DoughRepository(application.doughDataStore),
                    application.applicationScope
                )
            }
        }
    }
}
