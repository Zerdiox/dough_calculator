package com.example.doughcalculator.dough

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Custom accessibility actions are only reachable through the experimental API.
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class IngredientsCardTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var recipe by mutableStateOf(
        DefaultDoughRecipe.copy(
            ingredients = listOf(
                Ingredient("Salt", Percentage(hundredths = 300, decimals = 1)),
                Ingredient("Yeast", Percentage(hundredths = 20, decimals = 2))
            )
        )
    )
    private var editor by mutableStateOf<IngredientListEditor?>(null)

    @Before
    fun showCard() {
        composeRule.setContent {
            IngredientsCard(
                recipe = recipe,
                editor = editor,
                savedIngredients = listOf("Honey", "Olive oil", "Salt", "Yeast"),
                onRecipeChange = { recipe = it },
                onStartEditing = { editor = IngredientListEditor(recipe.ingredients) },
                onEditorChange = { editor = it },
                onFinishEditing = {
                    editor?.toIngredients()?.let {
                        recipe = recipe.copy(ingredients = it)
                        editor = null
                    }
                }
            )
        }
    }

    private fun nameFields() = composeRule.onAllNodes(hasSetTextAction())

    // The typed text only, leaving out the field's placeholder.
    private fun SemanticsNodeInteraction.typedText() =
        fetchSemanticsNode().config[SemanticsProperties.EditableText].text

    private fun assertNames(vararg names: String) {
        val fields = nameFields().fetchSemanticsNodes()
        assertEquals(
            names.toList(),
            fields.map {
                it.config[SemanticsProperties.EditableText].text
            }
        )
    }

    private fun addIngredient() = composeRule.onNodeWithText("Add ingredient").performClick()

    @Test
    fun outsideEditModeEveryValueHasAStepper() {
        composeRule.onNodeWithText("Hydration").assertExists()
        listOf("Hydration", "Salt", "Yeast").forEach {
            composeRule.onNodeWithContentDescription("Increase $it").assertExists()
        }
        composeRule.onNodeWithText("Edit").assertIsEnabled()
        composeRule.onNodeWithText("In every recipe").assertDoesNotExist()
    }

    @Test
    fun editModeShowsNameFieldsHandlesAndRemoveButtons() {
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onNodeWithText("Hydration").assertExists()
        composeRule.onNodeWithText("In every recipe").assertExists()
        composeRule.onNodeWithContentDescription("Increase Hydration").assertDoesNotExist()
        assertNames("Salt", "Yeast")
        listOf("Salt", "Yeast").forEach {
            composeRule.onNodeWithContentDescription("Reorder $it").assertExists()
            composeRule.onNodeWithContentDescription("Remove $it").assertExists()
        }
        composeRule.onNodeWithText("Done").assertIsEnabled()
    }

    @Test
    fun addIngredientAddsABlankRowThatDisablesDoneWithoutAMessage() {
        composeRule.onNodeWithText("Edit").performClick()
        addIngredient()
        assertNames("Salt", "Yeast", "")
        composeRule.onNodeWithText("Done").assertIsNotEnabled()
        composeRule.onNodeWithText("already", substring = true).assertDoesNotExist()
    }

    @Test
    fun aDuplicateNameShowsItsMessageAndDisablesDone() {
        composeRule.onNodeWithText("Edit").performClick()
        addIngredient()
        nameFields().onLast().performTextInput("salt")
        composeRule.onNodeWithText("\"Salt\" is already in this recipe").assertExists()
        composeRule.onNodeWithText("Done").assertIsNotEnabled()
    }

    @Test
    fun removeTakesTheRowOut() {
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onNodeWithContentDescription("Remove Salt").performClick()
        assertNames("Yeast")
    }

    @Test
    fun accessibilityActionsMoveRows() {
        composeRule.onNodeWithText("Edit").performClick()
        composeRule.onNodeWithContentDescription("Reorder Yeast")
            .performCustomAccessibilityActionWithLabel("Move up")
        assertNames("Yeast", "Salt")
        composeRule.onNodeWithContentDescription("Reorder Yeast")
            .performCustomAccessibilityActionWithLabel("Move down")
        assertNames("Salt", "Yeast")
    }

    @Test
    fun doneAppliesTheNames() {
        composeRule.onNodeWithText("Edit").performClick()
        addIngredient()
        nameFields().onLast().performTextInput("Malt")
        composeRule.onNodeWithText("Done").performClick()
        assertEquals(listOf("Salt", "Yeast", "Malt"), recipe.ingredients.map { it.name })
        composeRule.onNodeWithContentDescription("Increase Malt").assertExists()
    }

    @Test
    fun aNewRowsDropdownOffersSavedNamesNotInTheRecipe() {
        composeRule.onNodeWithText("Edit").performClick()
        addIngredient()
        nameFields().onLast().performClick()
        composeRule.onNodeWithText("Honey").assertExists()
        composeRule.onNodeWithText("Olive oil").performClick()
        assertEquals("Olive oil", nameFields().onLast().typedText())
        composeRule.onNodeWithText("Honey").assertDoesNotExist()
    }

    @Test
    fun tappingAFocusedNewRowKeepsTheDropdownOpen() {
        composeRule.onNodeWithText("Edit").performClick()
        addIngredient()
        nameFields().onLast().performClick()
        nameFields().onLast().performClick()
        composeRule.onNodeWithText("Honey").assertExists()
    }

    @Test
    fun aDropdownSaysWhenNothingMatches() {
        composeRule.onNodeWithText("Edit").performClick()
        addIngredient()
        nameFields().onLast().performTextInput("Truffle")
        composeRule.onNodeWithText("No saved ingredient matches").assertExists()
    }

    @Test
    fun rowsFromBeforeEditingHaveNoDropdown() {
        composeRule.onNodeWithText("Edit").performClick()
        nameFields()[0].performClick()
        composeRule.onNodeWithText("Honey").assertDoesNotExist()
        composeRule.onNodeWithText("Olive oil").assertDoesNotExist()
    }
}
