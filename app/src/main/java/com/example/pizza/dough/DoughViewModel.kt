package com.example.pizza.dough

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pizza.PizzaApplication
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Holds the calculator and saved recipes for the screens. Writes run in [writeScope], which outlives
 * this ViewModel, so a write still finishes when the screen that started it closes.
 */
class DoughViewModel(
    private val repository: DoughRepository,
    private val writeScope: CoroutineScope
) : ViewModel() {
    private val editedRecipe = MutableStateFlow<DoughRecipe?>(null)
    private var calculatorWrite: Job? = null

    /** The recipe on the calculator, or `null` while it is still loading. */
    val recipe: StateFlow<DoughRecipe?> =
        combine(repository.data, editedRecipe) { data, edited -> edited ?: data.currentRecipe }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** The saved recipes, or `null` while they are still loading. */
    val savedRecipes: StateFlow<List<SavedRecipe>?> = repository.data
        .map { it.savedRecipes }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** The saved recipe the calculator was loaded from, or `null` if none or it was deleted. */
    val loadedRecipe: StateFlow<SavedRecipe?> = repository.data
        .map { it.loadedRecipe }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** Whether the user still has to be told that unreadable data was set aside. */
    val resetMessagePending: StateFlow<Boolean> = repository.data
        .map { it.resetMessagePending }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), false)

    fun updateRecipe(recipe: DoughRecipe) {
        editedRecipe.value = recipe
        // Saving waits for a short pause, so dragging a slider doesn't write to disk every frame.
        writeCalculator {
            delay(SAVE_DEBOUNCE_MILLIS)
            repository.saveCurrentRecipe(recipe)
        }
    }

    fun loadRecipe(savedRecipe: SavedRecipe) {
        editedRecipe.value = savedRecipe.recipe
        // Not cancellable: a change made right after must not cut off the link to the recipe.
        writeCalculator { withContext(NonCancellable) { repository.loadRecipe(savedRecipe) } }
    }

    fun saveRecipe(name: String, recipe: DoughRecipe) {
        writeScope.launch { repository.saveRecipe(name, recipe) }
    }

    fun updateSavedRecipe(id: String, recipe: DoughRecipe) {
        writeScope.launch { repository.updateSavedRecipe(id, recipe) }
    }

    fun dismissResetMessage() {
        writeScope.launch { repository.dismissResetMessage() }
    }

    fun deleteRecipe(id: String) {
        writeScope.launch { repository.deleteRecipe(id) }
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
                        as PizzaApplication
                DoughViewModel(
                    DoughRepository(application.doughDataStore),
                    application.applicationScope
                )
            }
        }
    }
}
