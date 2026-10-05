package com.example.pizza.dough

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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
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
    private val store = ViewModelStore()

    private val repository by lazy {
        val dataFile = File(folder.root, "dough.json").apply {
            val stored = DoughData(savedRecipes = listOf(party))
            outputStream().use { runBlocking { DoughDataSerializer().writeTo(stored, it) } }
        }
        DoughRepository(createDoughDataStore(dataFile, scope))
    }

    private val viewModel by lazy {
        val factory = viewModelFactory { initializer { DoughViewModel(repository, scope) } }
        ViewModelProvider.create(store, factory)[DoughViewModel::class]
    }

    @After
    fun tearDown() {
        store.clear()
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

    private companion object {
        const val TIMEOUT_MILLIS = 1_000L
        val party = SavedRecipe(
            id = "b2",
            name = "Party",
            recipe = DefaultDoughRecipe.copy(portionCount = 10, portionWeightGrams = 300)
        )
    }
}
