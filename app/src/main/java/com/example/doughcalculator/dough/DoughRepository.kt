package com.example.doughcalculator.dough

import androidx.datastore.core.DataStore
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

/**
 * Everything the app stores: the recipe on the calculator, the saved recipes, the saved recipe the
 * calculator was loaded from, whether the user still has to be told that unreadable data was set
 * aside, and the ingredient names offered when adding an ingredient.
 */
@Serializable
data class DoughData(
    val currentRecipe: DoughRecipe = DefaultDoughRecipe,
    val savedRecipes: List<SavedRecipe> = emptyList(),
    val resetMessagePending: Boolean = false,
    val loadedRecipeId: String? = null,
    // Unsorted: wherever they are shown, they are sorted.
    val savedIngredients: List<String> = DefaultSavedIngredients
) {
    /** The saved recipe the calculator was loaded from, or `null` once it is deleted. */
    val loadedRecipe: SavedRecipe? get() = savedRecipes.find { it.id == loadedRecipeId }
}

private val DefaultSavedIngredients = listOf("Olive oil", "Salt", "Yeast")

@Serializable
data class SavedRecipe(val id: String, val name: String, val recipe: DoughRecipe)

class DoughRepository(private val dataStore: DataStore<DoughData>) {
    val data: Flow<DoughData> = dataStore.data
        .catch { error -> if (error is IOException) emit(DoughData()) else throw error }

    suspend fun saveCurrentRecipe(recipe: DoughRecipe) {
        dataStore.updateData { it.copy(currentRecipe = recipe) }
    }

    /** The saved ingredients, sorted the way they are shown. */
    val savedIngredients: Flow<List<String>> =
        data.map { it.savedIngredients.sortedWith(String.CASE_INSENSITIVE_ORDER) }

    /** Saves [recipe] as new and adds [namesToSave] to the saved ingredients, in one write. */
    suspend fun saveRecipe(
        name: String,
        recipe: DoughRecipe,
        namesToSave: Set<String> = emptySet()
    ) {
        // Created outside updateData, whose transform may run more than once.
        val savedRecipe =
            SavedRecipe(id = UUID.randomUUID().toString(), name = name, recipe = recipe)
        dataStore.updateData {
            it.copy(
                savedRecipes = it.savedRecipes + savedRecipe,
                loadedRecipeId = savedRecipe.id,
                savedIngredients = it.savedIngredients.withNames(namesToSave)
            )
        }
    }

    /** Puts [savedRecipe] on the calculator and links them in one write, so they always agree. */
    suspend fun loadRecipe(savedRecipe: SavedRecipe) {
        dataStore.updateData {
            it.copy(currentRecipe = savedRecipe.recipe, loadedRecipeId = savedRecipe.id)
        }
    }

    /**
     * Replaces the values of the saved recipe [id] and adds [namesToSave] to the saved ingredients,
     * in one write. Does nothing if the recipe no longer exists.
     */
    suspend fun updateSavedRecipe(
        id: String,
        recipe: DoughRecipe,
        namesToSave: Set<String> = emptySet()
    ) {
        dataStore.updateData { data ->
            if (data.savedRecipes.none { it.id == id }) return@updateData data
            data.copy(
                savedRecipes = data.savedRecipes.map {
                    if (it.id == id) it.copy(recipe = recipe) else it
                },
                savedIngredients = data.savedIngredients.withNames(namesToSave)
            )
        }
    }

    suspend fun deleteSavedIngredient(name: String) {
        dataStore.updateData { data ->
            data.copy(savedIngredients = data.savedIngredients - name)
        }
    }

    /** Adds [name] back to the saved ingredients, unless it is already there in any case. */
    suspend fun restoreSavedIngredient(name: String) {
        dataStore.updateData { data ->
            data.copy(savedIngredients = data.savedIngredients.withNames(setOf(name)))
        }
    }

    suspend fun dismissResetMessage() {
        dataStore.updateData { it.copy(resetMessagePending = false) }
    }

    /**
     * Puts the deleted [savedRecipe] back at [index], or last if the list is shorter. Does nothing
     * if it is still there, so restoring twice can't make a copy.
     */
    suspend fun restoreRecipe(savedRecipe: SavedRecipe, index: Int) {
        dataStore.updateData { data ->
            val recipes = data.savedRecipes
            if (recipes.any { it.id == savedRecipe.id }) return@updateData data
            val position = index.coerceIn(0, recipes.size)
            data.copy(savedRecipes = recipes.take(position) + savedRecipe + recipes.drop(position))
        }
    }

    suspend fun deleteRecipe(id: String) {
        dataStore.updateData { data ->
            data.copy(savedRecipes = data.savedRecipes.filterNot { it.id == id })
        }
    }
}

/**
 * These names plus those of [names] not among them, ignoring case, so a repeated tick or Undo can't
 * add a name twice.
 */
private fun List<String>.withNames(names: Set<String>): List<String> =
    names.fold(this) { saved, name ->
        if (saved.any { it.isSameNameAs(name) }) saved else saved + name
    }
