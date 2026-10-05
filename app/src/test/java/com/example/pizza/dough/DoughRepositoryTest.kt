package com.example.pizza.dough

import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Loading, saving, updating and restoring recipes keep the calculator linked to the right recipe.
 */
class DoughRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dataFile by lazy {
        File(folder.root, "dough.json").apply {
            outputStream().use { runBlocking { DoughDataSerializer().writeTo(storedData, it) } }
        }
    }

    private val repository by lazy { DoughRepository(createDoughDataStore(dataFile, scope)) }

    @After
    fun closeDataStore() {
        scope.cancel()
    }

    @Test
    fun loadingStoresTheValuesAndTheLinkTogether() = runBlocking {
        repository.loadRecipe(party)
        // One write: the file on disk holds both, not just the data the repository caches.
        val stored = DoughDataSerializer().readFrom(dataFile.inputStream())
        assertEquals(party.recipe, stored.currentRecipe)
        assertEquals(party.id, stored.loadedRecipeId)
    }

    @Test
    fun savingAsNewLinksTheNewRecipe() = runBlocking {
        repository.loadRecipe(fridayNight)
        repository.saveRecipe("Wetter Friday", wetter)
        val data = repository.data.first()
        val added = data.savedRecipes.last()
        assertEquals("Wetter Friday", added.name)
        assertEquals(wetter, added.recipe)
        assertEquals(added.id, data.loadedRecipeId)
    }

    @Test
    fun updatingReplacesTheValuesInPlace() = runBlocking {
        repository.updateSavedRecipe(fridayNight.id, wetter)
        val expected = listOf(fridayNight.copy(recipe = wetter), party)
        assertEquals(expected, repository.data.first().savedRecipes)
    }

    @Test
    fun updatingADeletedRecipeChangesNothing() = runBlocking {
        repository.deleteRecipe(party.id)
        val before = repository.data.first()
        repository.updateSavedRecipe(party.id, wetter)
        assertEquals(before, repository.data.first())
    }

    @Test
    fun loadedRecipeIsFoundWhileItExists() = runBlocking {
        repository.loadRecipe(party)
        assertEquals(party, repository.data.first().loadedRecipe)
        repository.deleteRecipe(party.id)
        assertNull(repository.data.first().loadedRecipe)
    }

    @Test
    fun restoringPutsTheRecipeBackInItsPlace() = runBlocking {
        repository.deleteRecipe(fridayNight.id)
        repository.restoreRecipe(fridayNight, index = 0)
        assertEquals(storedData.savedRecipes, repository.data.first().savedRecipes)
    }

    @Test
    fun restoringPastTheEndPutsTheRecipeLast() = runBlocking {
        repository.deleteRecipe(fridayNight.id)
        repository.restoreRecipe(fridayNight, index = 5)
        assertEquals(listOf(party, fridayNight), repository.data.first().savedRecipes)
    }

    @Test
    fun restoringBeforeTheStartPutsTheRecipeFirst() = runBlocking {
        repository.deleteRecipe(fridayNight.id)
        repository.restoreRecipe(fridayNight, index = -1)
        assertEquals(listOf(fridayNight, party), repository.data.first().savedRecipes)
    }

    @Test
    fun restoringARecipeThatIsStillThereChangesNothing() = runBlocking {
        val before = repository.data.first()
        repository.restoreRecipe(party, index = 0)
        assertEquals(before, repository.data.first())
    }

    @Test
    fun restoringTheLoadedRecipeLinksItAgain() = runBlocking {
        repository.loadRecipe(party)
        repository.deleteRecipe(party.id)
        repository.restoreRecipe(party, index = 1)
        assertEquals(party, repository.data.first().loadedRecipe)
    }

    @Test
    fun nothingIsLoadedAtFirst() = runBlocking {
        assertNull(repository.data.first().loadedRecipe)
    }

    private companion object {
        val fridayNight = SavedRecipe(
            id = "a1",
            name = "Friday night",
            recipe = DefaultDoughRecipe
        )
        val party = SavedRecipe(
            id = "b2",
            name = "Party",
            recipe = DefaultDoughRecipe.copy(portionCount = 10, portionWeightGrams = 300)
        )
        val wetter = DefaultDoughRecipe.copy(hydrationPercent = 65f)
        val storedData = DoughData(savedRecipes = listOf(fridayNight, party))
    }
}
