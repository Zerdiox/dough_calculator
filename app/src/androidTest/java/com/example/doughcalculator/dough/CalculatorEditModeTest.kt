package com.example.doughcalculator.dough

import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorEditModeTest {
    // An activity rule, so a test can see Back passing the screen on to the activity.
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var recipe by mutableStateOf(
        DefaultDoughRecipe.copy(
            ingredients = listOf("Salt", "Yeast", "Honey").map {
                Ingredient(it, Percentage(hundredths = 100, decimals = 0))
            }
        )
    )
    private var editor by mutableStateOf<IngredientListEditor?>(null)
    private var backReachedActivity = false

    @Before
    fun showCalculator() {
        // Registered before the screen's own handler, so it only runs when the screen lets Back
        // through.
        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.addCallback(
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        backReachedActivity = true
                    }
                }
            )
        }
        composeRule.setContent {
            DoughCalculatorContent(
                recipe = recipe,
                loadedRecipeName = null,
                ingredientEditor = editor,
                savedIngredients = emptyList(),
                resetMessagePending = false,
                onRecipeChange = { recipe = it },
                onStartEditing = { editor = IngredientListEditor(recipe.ingredients) },
                onEditorChange = { editor = it },
                onFinishEditing = {
                    editor?.toIngredients()?.let {
                        recipe = recipe.copy(ingredients = it)
                        editor = null
                    }
                },
                onSaveRecipe = { _, _ -> },
                onUpdateLoadedRecipe = {},
                onOpenSavedRecipes = {},
                onOpenConvert = {},
                onOpenFullRecipe = {},
                onDismissResetMessage = {}
            )
        }
    }

    // Name fields, leaving out the portion weight field.
    private val nameField = hasSetTextAction() and !hasContentDescription("Portion weight")

    private fun names() = composeRule.onAllNodes(nameField).fetchSemanticsNodes()
        .map { it.config[SemanticsProperties.EditableText].text }

    private fun startEditing() = composeRule.onNodeWithText("Edit").performClick()

    private fun pressBack() {
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        composeRule.waitForIdle()
    }

    @Test
    fun saveSavedRecipesAndStartKneadingAreDisabledWhileEditing() {
        startEditing()
        composeRule.onNodeWithContentDescription("Save recipe").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Saved recipes").assertIsNotEnabled()
        composeRule.onNodeWithText("Start kneading").performScrollTo().assertIsNotEnabled()
        composeRule.onNodeWithText("Done").performScrollTo().performClick()
        composeRule.onNodeWithContentDescription("Save recipe").assertIsEnabled()
        composeRule.onNodeWithContentDescription("Saved recipes").assertIsEnabled()
        composeRule.onNodeWithText("Start kneading").performScrollTo().assertIsEnabled()
    }

    @Test
    fun convertIsDisabledWhileEditing() {
        startEditing()
        composeRule.onNodeWithContentDescription("Convert recipe").assertIsNotEnabled()
        composeRule.onNodeWithText("Done").performScrollTo().performClick()
        composeRule.onNodeWithContentDescription("Convert recipe").assertIsEnabled()
    }

    @Test
    fun removingShowsAMessageWhoseUndoPutsTheRowBack() {
        startEditing()
        composeRule.onNodeWithContentDescription("Remove Yeast").performClick()
        assertEquals(listOf("Salt", "Honey"), names())
        composeRule.onNodeWithText("Removed \"Yeast\"").assertExists()
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(listOf("Salt", "Yeast", "Honey"), names())
        composeRule.onNodeWithText("Removed \"Yeast\"").assertDoesNotExist()
    }

    @Test
    fun aSecondRemovalReplacesTheMessage() {
        startEditing()
        composeRule.onNodeWithContentDescription("Remove Yeast").performClick()
        composeRule.onNodeWithContentDescription("Remove Salt").performClick()
        composeRule.onNodeWithText("Removed \"Yeast\"").assertDoesNotExist()
        composeRule.onNodeWithText("Removed \"Salt\"").assertExists()
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(listOf("Salt", "Honey"), names())
    }

    @Test
    fun doneEndsTheMessage() {
        startEditing()
        composeRule.onNodeWithContentDescription("Remove Yeast").performClick()
        composeRule.onNodeWithText("Done").performClick()
        composeRule.onNodeWithText("Removed \"Yeast\"").assertDoesNotExist()
        assertEquals(listOf("Salt", "Honey"), recipe.ingredients.map { it.name })
    }

    @Test
    fun backActsAsDone() {
        startEditing()
        composeRule.onNodeWithContentDescription("Remove Yeast").performClick()
        pressBack()
        assertNull(editor)
        assertEquals(listOf("Salt", "Honey"), recipe.ingredients.map { it.name })
    }

    @Test
    fun backDoesNothingWhileDoneIsDisabled() {
        startEditing()
        // A blank name disables Done; nothing is focused, so Back goes straight to the screen.
        composeRule.onNodeWithText("Add ingredient").performScrollTo().performClick()
        pressBack()
        assertFalse(backReachedActivity)
        assertNotNull(editor)
        composeRule.onNodeWithText("Done").assertIsNotEnabled()
    }
}
