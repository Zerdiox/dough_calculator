package com.example.pizza.dough

import androidx.datastore.core.DataStore
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.Serializable

/**
 * Everything the app stores: the recipe on the calculator, the saved recipes, and whether the user
 * still has to be told that unreadable data was set aside.
 */
@Serializable
data class DoughData(
    val currentRecipe: DoughRecipe = DefaultDoughRecipe,
    val savedRecipes: List<SavedRecipe> = emptyList(),
    val resetMessagePending: Boolean = false
)

@Serializable
data class SavedRecipe(val id: String, val name: String, val recipe: DoughRecipe)

class DoughRepository(private val dataStore: DataStore<DoughData>) {
    val data: Flow<DoughData> = dataStore.data
        .catch { error -> if (error is IOException) emit(DoughData()) else throw error }

    suspend fun saveCurrentRecipe(recipe: DoughRecipe) {
        dataStore.updateData { it.copy(currentRecipe = recipe) }
    }

    suspend fun saveRecipe(name: String, recipe: DoughRecipe) {
        // Created outside updateData, whose transform may run more than once.
        val savedRecipe =
            SavedRecipe(id = UUID.randomUUID().toString(), name = name, recipe = recipe)
        dataStore.updateData { it.copy(savedRecipes = it.savedRecipes + savedRecipe) }
    }

    suspend fun dismissResetMessage() {
        dataStore.updateData { it.copy(resetMessagePending = false) }
    }

    suspend fun deleteRecipe(id: String) {
        dataStore.updateData { data ->
            data.copy(savedRecipes = data.savedRecipes.filterNot { it.id == id })
        }
    }
}
