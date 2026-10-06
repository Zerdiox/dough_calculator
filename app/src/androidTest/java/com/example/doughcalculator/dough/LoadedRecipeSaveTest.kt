package com.example.doughcalculator.dough

import android.view.KeyEvent
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadedRecipeSaveTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val updates = mutableListOf<Set<String>>()
    private val saves = mutableListOf<Pair<String, Set<String>>>()
    private val updateCount get() = updates.size
    private val savedNames get() = saves.map { it.first }

    private fun tapSave(
        loadedRecipeName: String?,
        recipe: DoughRecipe = DefaultDoughRecipe,
        savedIngredients: List<String> = emptyList()
    ) {
        composeRule.setContent {
            DoughCalculatorContent(
                recipe = recipe,
                loadedRecipeName = loadedRecipeName,
                ingredientEditor = null,
                savedIngredients = savedIngredients,
                resetMessagePending = false,
                onRecipeChange = {},
                onStartEditing = {},
                onEditorChange = {},
                onFinishEditing = {},
                onSaveRecipe = { name, namesToSave -> saves += name to namesToSave },
                onUpdateLoadedRecipe = { updates += it },
                onOpenSavedRecipes = {},
                onOpenConvert = {},
                onOpenFullRecipe = {},
                onDismissResetMessage = {}
            )
        }
        composeRule.onNodeWithContentDescription("Save recipe").performClick()
    }

    @Test
    fun loadedRecipeOffersUpdateAndSaveAsNew() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Update \"Friday night\"").assertExists()
        composeRule.onNodeWithText("Save as new…").assertExists()
    }

    @Test
    fun updateUpdatesOnceAndConfirms() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Update \"Friday night\"").performClick()
        assertEquals(1, updateCount)
        assertEquals(emptyList<String>(), savedNames)
        composeRule.onNodeWithText("Updated \"Friday night\"").assertExists()
    }

    @Test
    fun saveAsNewAsksForAName() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Save as new…").performClick()
        composeRule.onNodeWithText("Name").performTextInput("Wetter Friday")
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(listOf("Wetter Friday"), savedNames)
        assertEquals(0, updateCount)
    }

    @Test
    fun cancelSavesNothing() {
        tapSave(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Update \"Friday night\"").assertDoesNotExist()
        assertSavedNothing()
    }

    @Test
    fun backSavesNothing() {
        tapSave(loadedRecipeName = "Friday night")
        // A real Back key goes to the focused window, the dialog. Espresso's pressBack waits for
        // the calculator's window to get focus, which it never does while a dialog it opened is
        // shown.
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Update \"Friday night\"").assertDoesNotExist()
        assertSavedNothing()
    }

    @Test
    fun withoutALoadedRecipeTheNameDialogOpensDirectly() {
        tapSave(loadedRecipeName = null)
        composeRule.onNodeWithText("Name").assertExists()
        composeRule.onNodeWithText("Save as new…").assertDoesNotExist()
    }

    @Test
    fun nameDialogListsTheNewNamesUnticked() {
        tapSaveWithNewNames(loadedRecipeName = null)
        composeRule.onNodeWithText("Add to saved ingredients").assertExists()
        checkbox("Honey").assertIsOff()
        checkbox("Truffle oil").assertIsOff()
        // Saved already, in another case.
        checkbox("Salt").assertDoesNotExist()
    }

    @Test
    fun nameDialogReportsTheTickedNames() {
        tapSaveWithNewNames(loadedRecipeName = null)
        checkbox("Honey").performClick()
        composeRule.onNodeWithText("Name").performTextInput("Honey night")
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(listOf("Honey night" to setOf("Honey")), saves)
    }

    @Test
    fun savingWithoutTicksReportsNoNames() {
        tapSaveWithNewNames(loadedRecipeName = null)
        composeRule.onNodeWithText("Name").performTextInput("Truffle night")
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(listOf("Truffle night" to emptySet<String>()), saves)
    }

    @Test
    fun updateReportsTheTickedNames() {
        tapSaveWithNewNames(loadedRecipeName = "Friday night")
        composeRule.onNodeWithText("Add to saved ingredients").assertExists()
        checkbox("Truffle oil").assertIsOff()
        checkbox("Honey").assertIsOff().performClick()
        composeRule.onNodeWithText("Update \"Friday night\"").performClick()
        assertEquals(listOf(setOf("Honey")), updates)
        assertEquals(emptyList<String>(), savedNames)
    }

    @Test
    fun ticksCarryIntoTheNameDialog() {
        tapSaveWithNewNames(loadedRecipeName = "Friday night")
        checkbox("Honey").performClick()
        composeRule.onNodeWithText("Save as new…").performClick()
        checkbox("Honey").assertIsOn()
        checkbox("Truffle oil").assertIsOff()
        composeRule.onNodeWithText("Name").performTextInput("Honey night")
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(listOf("Honey night" to setOf("Honey")), saves)
        assertEquals(0, updateCount)
    }

    @Test
    fun withNoNewNamesNoListShows() {
        tapSave(
            loadedRecipeName = "Friday night",
            recipe = recipeWith("Salt", "Yeast"),
            savedIngredients = listOf("Olive oil", "Salt", "Yeast")
        )
        composeRule.onNodeWithText("Add to saved ingredients").assertDoesNotExist()
        composeRule.onNodeWithText("Save as new…").performClick()
        composeRule.onNodeWithText("Name").assertExists()
        composeRule.onNodeWithText("Add to saved ingredients").assertDoesNotExist()
    }

    @Test
    fun cancelAfterTickingReportsNothingAndClearsTheTicks() {
        tapSaveWithNewNames(loadedRecipeName = null)
        checkbox("Honey").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        assertSavedNothing()
        composeRule.onNodeWithContentDescription("Save recipe").performClick()
        checkbox("Honey").assertIsOff()
    }

    private fun tapSaveWithNewNames(loadedRecipeName: String?) {
        tapSave(
            loadedRecipeName = loadedRecipeName,
            recipe = recipeWith("Salt", "Honey", "Truffle oil"),
            savedIngredients = listOf("Olive oil", "salt", "Yeast")
        )
    }

    // The calculator behind the dialog shows the same names, so match only the checkbox rows.
    private fun checkbox(name: String) = composeRule.onNode(hasText(name) and isToggleable())

    private fun recipeWith(vararg names: String) = DefaultDoughRecipe.copy(
        ingredients = names.map { Ingredient(it, Percentage(hundredths = 100, decimals = 0)) }
    )

    private fun assertSavedNothing() {
        assertEquals(0, updateCount)
        assertEquals(emptyList<String>(), savedNames)
    }
}
