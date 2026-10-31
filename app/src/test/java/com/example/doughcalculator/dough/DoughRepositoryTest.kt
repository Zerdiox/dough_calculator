package com.example.doughcalculator.dough

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
    fun savingAsNewKeepsTheNote() = runBlocking {
        repository.saveRecipe("Noted", wetter.copy(notes = "Mix"))
        assertEquals("Mix", repository.data.first().savedRecipes.last().recipe.notes)
    }

    @Test
    fun updatingChangesTheNoteOfThatRecipeOnly() = runBlocking {
        repository.updateSavedRecipe(fridayNight.id, fridayNight.recipe.copy(notes = "Mix"))
        val recipes = repository.data.first().savedRecipes
        assertEquals("Mix", recipes.first { it.id == fridayNight.id }.recipe.notes)
        assertEquals("", recipes.first { it.id == party.id }.recipe.notes)
    }

    @Test
    fun loadingReplacesTheCalculatorNote() = runBlocking {
        repository.saveCurrentRecipe(party.recipe.copy(notes = "Old"))
        repository.loadRecipe(fridayNight)
        assertEquals("", repository.data.first().currentRecipe.notes)
    }

    @Test
    fun usingARecipeClearsTheCalculatorNote() = runBlocking {
        repository.saveCurrentRecipe(party.recipe.copy(notes = "Old"))
        repository.useRecipe(wetter)
        assertEquals("", repository.data.first().currentRecipe.notes)
    }

    @Test
    fun restoringADeletedRecipeKeepsItsNote() = runBlocking {
        val noted = fridayNight.copy(recipe = fridayNight.recipe.copy(notes = "Mix"))
        repository.updateSavedRecipe(noted.id, noted.recipe)
        repository.deleteRecipe(noted.id)
        repository.restoreRecipe(noted, 0)
        assertEquals("Mix", repository.data.first().savedRecipes.first().recipe.notes)
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
    fun savingWithoutLinkingLeavesTheCalculatorAlone() = runBlocking {
        repository.loadRecipe(fridayNight)
        repository.saveRecipe("Book", wetter, setOf("Malt"), linkCalculator = false)
        val data = repository.data.first()
        assertEquals("Book", data.savedRecipes.last().name)
        assertEquals(wetter, data.savedRecipes.last().recipe)
        assertEquals(fridayNight.id, data.loadedRecipeId)
        assertEquals(fridayNight.recipe, data.currentRecipe)
        assertEquals(true, SavedIngredient("Malt") in data.savedIngredients)
    }

    @Test
    fun usingARecipeStoresItAndDropsTheLinkTogether() = runBlocking {
        repository.loadRecipe(fridayNight)
        repository.useRecipe(wetter)
        // One write: the file on disk holds both, not just the data the repository caches.
        val stored = DoughDataSerializer().readFrom(dataFile.inputStream())
        assertEquals(wetter, stored.currentRecipe)
        assertNull(stored.loadedRecipeId)
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
        repository.updateSavedRecipe(party.id, wetter, namesToSave = setOf("Honey"))
        assertEquals(before, repository.data.first())
    }

    private val DoughData.savedNames get() = savedIngredients.map { it.name }

    private val withEggs = DefaultDoughRecipe.copy(
        ingredients = listOf(Ingredient("Eggs", Percentage(0, 0), IngredientCount(4, 4, 5000)))
    )

    @Test
    fun tickedCountIsSavedWithItsKindAndGramsEach() = runBlocking {
        repository.saveRecipe("Eggy", withEggs, namesToSave = setOf("Eggs"))
        assertEquals(
            SavedIngredient("Eggs", isCount = true, gramsEachHundredths = 5000),
            repository.data.first().savedIngredients.last()
        )
    }

    @Test
    fun updatingWithATickedCountSavesItsKindToo() = runBlocking {
        repository.updateSavedRecipe(fridayNight.id, withEggs, namesToSave = setOf("Eggs"))
        assertEquals(true, repository.data.first().savedIngredients.last().isCount)
    }

    @Test
    fun savingARecipeLeavesAnAlreadySavedIngredientAsItIs() = runBlocking {
        val saved = SavedIngredient("Eggs", isCount = true, gramsEachHundredths = 5000)
        repository.restoreSavedIngredient(saved)
        val heavier = DefaultDoughRecipe.copy(
            ingredients = listOf(Ingredient("Eggs", Percentage(0, 0), IngredientCount(4, 4, 6000)))
        )
        repository.saveRecipe("Heavy", heavier, namesToSave = setOf("Eggs"))
        assertEquals(saved, repository.data.first().savedIngredients.last())
    }

    @Test
    fun restoringBringsBackKindAndGramsEach() = runBlocking {
        val saved = SavedIngredient("Eggs", isCount = true, gramsEachHundredths = 5000)
        repository.restoreSavedIngredient(saved)
        repository.deleteSavedIngredient("Eggs")
        repository.restoreSavedIngredient(saved)
        assertEquals(saved, repository.data.first().savedIngredients.last())
    }

    @Test
    fun savingWithNamesStoresTheRecipeAndTheNamesTogether() = runBlocking {
        repository.saveRecipe("Honey", withHoney, namesToSave = setOf("Honey"))
        // One write: the file on disk holds both, not just the data the repository caches.
        val stored = DoughDataSerializer().readFrom(dataFile.inputStream())
        assertEquals(withHoney, stored.savedRecipes.last().recipe)
        assertEquals(listOf("Olive oil", "Salt", "Yeast", "Honey"), stored.savedNames)
    }

    @Test
    fun updatingWithNamesStoresTheRecipeAndTheNamesTogether() = runBlocking {
        repository.updateSavedRecipe(fridayNight.id, withHoney, namesToSave = setOf("Honey"))
        val stored = DoughDataSerializer().readFrom(dataFile.inputStream())
        assertEquals(withHoney, stored.savedRecipes.first().recipe)
        assertEquals(listOf("Olive oil", "Salt", "Yeast", "Honey"), stored.savedNames)
    }

    @Test
    fun savingWithoutNamesKeepsTheSavedIngredients() = runBlocking {
        repository.saveRecipe("Honey", withHoney, namesToSave = emptySet())
        assertEquals(listOf("Olive oil", "Salt", "Yeast"), repository.data.first().savedNames)
    }

    @Test
    fun addingANameSavedInAnotherCaseChangesNothing() = runBlocking {
        val before = repository.data.first().savedIngredients
        repository.restoreSavedIngredient(SavedIngredient("salt"))
        repository.saveRecipe("Salty", DefaultDoughRecipe, namesToSave = setOf("YEAST"))
        assertEquals(before, repository.data.first().savedIngredients)
    }

    @Test
    fun deletedIngredientCanBeAddedBack() = runBlocking {
        repository.deleteSavedIngredient("Salt")
        assertEquals(listOf("Olive oil", "Yeast"), repository.data.first().savedNames)
        repository.restoreSavedIngredient(SavedIngredient("Salt"))
        assertEquals(
            listOf("Olive oil", "Salt", "Yeast"),
            repository.savedIngredients.first().map { it.name }
        )
    }

    @Test
    fun deletingASavedIngredientLeavesRecipesUnchanged() = runBlocking {
        repository.updateSavedRecipe(fridayNight.id, withHoney, namesToSave = setOf("Honey"))
        repository.loadRecipe(fridayNight.copy(recipe = withHoney))
        val before = repository.data.first()
        repository.deleteSavedIngredient("Honey")
        val after = repository.data.first()
        assertEquals(before.savedRecipes, after.savedRecipes)
        assertEquals(before.currentRecipe, after.currentRecipe)
    }

    @Test
    fun savedIngredientsAreSortedIgnoringCase() = runBlocking {
        repository.restoreSavedIngredient(SavedIngredient("honey"))
        repository.restoreSavedIngredient(SavedIngredient("Basil"))
        assertEquals(
            listOf("Basil", "honey", "Olive oil", "Salt", "Yeast"),
            repository.savedIngredients.first().map { it.name }
        )
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
        val wetter = DefaultDoughRecipe.copy(
            hydration = Percentage(hundredths = 6500, decimals = 0)
        )
        val withHoney = DefaultDoughRecipe.copy(
            ingredients = listOf(
                Ingredient(name = "Honey", percentage = Percentage(hundredths = 250, decimals = 1))
            )
        )
        val storedData = DoughData(savedRecipes = listOf(fridayNight, party))
    }
}
