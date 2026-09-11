package com.example.pizza.dough

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/** Everything the app stores: the recipe on the calculator and the saved recipes. */
@Serializable
data class DoughData(
    val currentRecipe: DoughRecipe = DefaultDoughRecipe,
    val savedRecipes: List<SavedRecipe> = emptyList()
)

@Serializable
data class SavedRecipe(val id: String, val name: String, val recipe: DoughRecipe)

val Context.doughDataStore: DataStore<DoughData> by dataStore(
    fileName = "dough.json",
    serializer = DoughDataSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler { DoughData() }
)

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

    suspend fun deleteRecipe(id: String) {
        dataStore.updateData { data ->
            data.copy(
                savedRecipes = data.savedRecipes.filterNot {
                    it.id ==
                        id
                }
            )
        }
    }
}

private object DoughDataSerializer : Serializer<DoughData> {
    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue = DoughData()

    override suspend fun readFrom(input: InputStream): DoughData = try {
        json.decodeFromString(DoughData.serializer(), input.readBytes().decodeToString())
    } catch (error: SerializationException) {
        throw CorruptionException("Cannot read saved dough data", error)
    }

    override suspend fun writeTo(t: DoughData, output: OutputStream) {
        output.write(json.encodeToString(DoughData.serializer(), t).encodeToByteArray())
    }
}
