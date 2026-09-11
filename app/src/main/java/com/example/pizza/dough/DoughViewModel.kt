package com.example.pizza.dough

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DoughViewModel(private val repository: DoughRepository) : ViewModel() {
    private val editedRecipe = MutableStateFlow<DoughRecipe?>(null)
    private var saveJob: Job? = null

    /** The recipe on the calculator, or `null` while it is still loading. */
    val recipe: StateFlow<DoughRecipe?> =
        combine(repository.data, editedRecipe) { data, edited -> edited ?: data.currentRecipe }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** The saved recipes, or `null` while they are still loading. */
    val savedRecipes: StateFlow<List<SavedRecipe>?> = repository.data
        .map { it.savedRecipes }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    fun updateRecipe(recipe: DoughRecipe) {
        editedRecipe.value = recipe
        // Saving waits for a short pause, so dragging a slider doesn't write to disk every frame.
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(SAVE_DEBOUNCE_MILLIS)
            repository.saveCurrentRecipe(recipe)
        }
    }

    fun saveRecipe(name: String, recipe: DoughRecipe) {
        viewModelScope.launch { repository.saveRecipe(name, recipe) }
    }

    fun deleteRecipe(id: String) {
        viewModelScope.launch { repository.deleteRecipe(id) }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L
        private const val SAVE_DEBOUNCE_MILLIS = 300L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                DoughViewModel(DoughRepository(application.doughDataStore))
            }
        }
    }
}
