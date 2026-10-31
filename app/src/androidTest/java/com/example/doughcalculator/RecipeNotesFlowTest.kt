package com.example.doughcalculator

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.doughcalculator.dough.DefaultDoughRecipe
import com.example.doughcalculator.dough.DoughData
import com.example.doughcalculator.dough.DoughDataSerializer
import com.example.doughcalculator.dough.DoughRepository
import com.example.doughcalculator.dough.DoughViewModel
import com.example.doughcalculator.dough.SavedRecipe
import com.example.doughcalculator.dough.createDoughDataStore
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

/** Notes through the real navigation, ViewModel and stored data, timing included. */
@RunWith(AndroidJUnit4::class)
class RecipeNotesFlowTest {
    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var repository: DoughRepository

    @Before
    fun showApp() {
        val friday = DefaultDoughRecipe.copy(notes = "<p>$SAVED_NOTE</p>")
        val stored = DoughData(
            currentRecipe = friday,
            savedRecipes = listOf(SavedRecipe(id = "a1", name = "Friday", recipe = friday)),
            loadedRecipeId = "a1"
        )
        val file = File(folder.root, "dough.json").apply {
            outputStream().use { runBlocking { DoughDataSerializer().writeTo(stored, it) } }
        }
        repository = DoughRepository(createDoughDataStore(file, scope))
        val viewModel = DoughViewModel(repository, scope)
        composeRule.setContent { DoughCalculatorApp(viewModel = viewModel) }
        waitForText(SAVED_NOTE)
    }

    @After
    fun closeDataStore() {
        scope.cancel()
    }

    private fun waitForText(text: String) = composeRule.waitUntil(TIMEOUT_MILLIS) {
        composeRule.onAllNodes(hasText(text, substring = true))
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    private fun typeInNotes(text: String) {
        composeRule.onNodeWithText("Notes").performScrollTo().performTextInput(text)
        waitForText(text)
    }

    private fun tap(description: String) {
        composeRule.onNodeWithContentDescription(description).clickDirectly()
    }

    private fun returnToCalculatorFromSavedRecipes() {
        tap("Back")
        waitForText("Notes")
    }

    @Test
    fun loadingReplacesAnUnsavedNote() {
        typeInNotes(UNSAVED_TEXT)
        tap("Saved recipes")
        tap("Edit in calculator")
        waitForText(SAVED_NOTE)
        composeRule.onNodeWithText(UNSAVED_TEXT, substring = true).assertDoesNotExist()
    }

    @Test
    fun usingAConvertedRecipeClearsTheNote() {
        tap("Convert recipe")
        composeRule.onNodeWithContentDescription("Flour").performTextInput("500")
        composeRule.onNodeWithText("Use in calculator").clickDirectly()
        waitForText("Notes")
        composeRule.onNodeWithText(SAVED_NOTE, substring = true).assertDoesNotExist()
    }

    @Test
    fun updatingStoresTheChangedNote() = runBlocking {
        typeInNotes(UNSAVED_TEXT)
        tap("Save recipe")
        composeRule.onNodeWithText("Update \"Friday\"").clickDirectly()
        composeRule.waitForIdle()
        withTimeout(TIMEOUT_MILLIS) {
            repository.data.first { data ->
                data.savedRecipes.single().recipe.notes.contains(UNSAVED_TEXT)
            }
        }
        Unit
    }

    @Test
    fun leavingAndReturningKeepsTheNote() {
        typeInNotes(UNSAVED_TEXT)
        tap("Saved recipes")
        returnToCalculatorFromSavedRecipes()
        composeRule.onNodeWithText(UNSAVED_TEXT, substring = true).assertExists()
    }

    private companion object {
        const val SAVED_NOTE = "Saved note"
        const val UNSAVED_TEXT = "Unsaved"
        const val TIMEOUT_MILLIS = 5_000L
    }
}
