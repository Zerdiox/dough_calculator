package com.example.doughcalculator.dough

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * The ViewModel's writes reach the stored data even when its screen closes. Its own scope can't run
 * on the JVM without a Main dispatcher, so only writes, which run in the given scope, are tested.
 */
class DoughViewModelTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Apart from the DataStore's scope, so waiting for the writes doesn't wait on its work.
    private val writeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val store = ViewModelStore()

    private val repository by lazy {
        val dataFile = File(folder.root, "dough.json").apply {
            val stored = DoughData(savedRecipes = listOf(party))
            outputStream().use { runBlocking { DoughDataSerializer().writeTo(stored, it) } }
        }
        DoughRepository(createDoughDataStore(dataFile, scope))
    }

    private val viewModel by lazy {
        val factory = viewModelFactory { initializer { DoughViewModel(repository, writeScope) } }
        ViewModelProvider.create(store, factory)[DoughViewModel::class]
    }

    @After
    fun tearDown() {
        store.clear()
        writeScope.cancel()
        scope.cancel()
    }

    @Test
    fun changeIsStoredWhenTheScreenClosesRightAfter() = runBlocking {
        val changed = DefaultDoughRecipe.copy(portionCount = 7)
        viewModel.updateRecipe(changed)
        store.clear()
        withTimeout(TIMEOUT_MILLIS) { repository.data.first { it.currentRecipe == changed } }
        Unit
    }

    @Test
    fun loadingReplacesAPendingChange() = runBlocking {
        viewModel.updateRecipe(DefaultDoughRecipe.copy(portionCount = 7))
        viewModel.loadRecipe(party)
        // Longer than the delay before a change is written, so a pending write would have landed.
        delay(TIMEOUT_MILLIS)
        val data = repository.data.first()
        assertEquals(party.recipe, data.currentRecipe)
        assertEquals(party.id, data.loadedRecipeId)
    }

    @Test
    fun savingWithNamesStoresTheRecipeAndTheNames() = runBlocking {
        viewModel.saveRecipe("Honey", DefaultDoughRecipe, namesToSave = setOf("Honey"))
        awaitWrites()
        val data = repository.data.first()
        assertEquals("Honey", data.savedRecipes.last().name)
        assertEquals(listOf("Olive oil", "Salt", "Yeast", "Honey"), data.savedIngredients)
    }

    @Test
    fun deletedIngredientCanBeRestored() = runBlocking {
        viewModel.deleteSavedIngredient("Salt")
        awaitWrites()
        assertEquals(listOf("Olive oil", "Yeast"), repository.data.first().savedIngredients)
        viewModel.restoreSavedIngredient("Salt")
        awaitWrites()
        assertEquals(listOf("Olive oil", "Yeast", "Salt"), repository.data.first().savedIngredients)
    }

    @Test
    fun startEditingBuildsAnEditorFromTheCalculatorRecipe() {
        viewModel.ingredientEditing.start(withSalt.ingredients)
        assertEquals(
            IngredientListEditor(withSalt.ingredients),
            viewModel.ingredientEditing.editor.value
        )
    }

    @Test
    fun doneWithAValidEditorUpdatesTheCalculatorAndEndsEditing() = runBlocking {
        viewModel.ingredientEditing.start(withSalt.ingredients)
        val editor = checkNotNull(viewModel.ingredientEditing.editor.value)
        viewModel.ingredientEditing.change(editor.rename(editor.rows.first().key, "Sea salt"))
        // Portions can still change while editing; Done keeps them.
        val current = withSalt.copy(portionCount = 6)
        viewModel.ingredientEditing.finish(current)
        assertNull(viewModel.ingredientEditing.editor.value)
        awaitWrites()
        val salt = withSalt.ingredients.first()
        assertEquals(
            current.copy(ingredients = listOf(salt.copy(name = "Sea salt"))),
            repository.data.first().currentRecipe
        )
    }

    @Test
    fun doneWithAnInvalidEditorChangesNothing() = runBlocking {
        viewModel.ingredientEditing.start(withSalt.ingredients)
        val invalid = checkNotNull(viewModel.ingredientEditing.editor.value).add()
        viewModel.ingredientEditing.change(invalid)
        viewModel.ingredientEditing.finish(withSalt)
        assertEquals(invalid, viewModel.ingredientEditing.editor.value)
        awaitWrites()
        assertEquals(DefaultDoughRecipe, repository.data.first().currentRecipe)
    }

    @Test
    fun editingDoesNotChangeASavedRecipe() = runBlocking {
        val loaded = party.copy(recipe = withSalt)
        viewModel.saveRecipe(loaded.name, loaded.recipe, namesToSave = emptySet())
        awaitWrites()
        val saved = repository.data.first().savedRecipes.last()
        viewModel.loadRecipe(saved)
        viewModel.ingredientEditing.start(saved.recipe.ingredients)
        val editor = checkNotNull(viewModel.ingredientEditing.editor.value)
        viewModel.ingredientEditing.change(editor.remove(editor.rows.first().key))
        viewModel.ingredientEditing.finish(saved.recipe)
        awaitWrites()
        val data = repository.data.first()
        assertEquals(emptyList<Ingredient>(), data.currentRecipe.ingredients)
        assertEquals(saved, data.savedRecipes.last())
    }

    // Waiting on the stored data instead can miss a write that lands while the wait starts.
    private suspend fun awaitWrites() = writeScope.coroutineContext.job.children.toList().joinAll()

    private companion object {
        const val TIMEOUT_MILLIS = 1_000L
        val party = SavedRecipe(
            id = "b2",
            name = "Party",
            recipe = DefaultDoughRecipe.copy(portionCount = 10, portionWeightGrams = 300)
        )
        val withSalt = DefaultDoughRecipe.copy(
            ingredients = listOf(Ingredient("Salt", Percentage(hundredths = 300, decimals = 1)))
        )
    }
}
