package com.example.doughcalculator.dough

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertRecipeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var draft by mutableStateOf(ConversionDraft(portionCount = 4))
    private val saves = mutableListOf<Triple<String, DoughRecipe, Set<String>>>()
    private val opened = mutableListOf<String>()
    private val used = mutableListOf<DoughRecipe>()

    // Grams fields carry a description for screen readers; name fields don't.
    private val nameField = hasSetTextAction() and
        SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription)

    @Before
    fun showScreen() {
        composeRule.setContent {
            ConvertRecipeScreen(
                draft = draft,
                savedIngredients = listOf("Salt", "Yeast"),
                onDraftChange = { draft = it },
                onUseInCalculator = { used += it },
                onSaveRecipe = { name, recipe, names -> saves += Triple(name, recipe, names) },
                onOpenSavedRecipe = { name, _ -> opened += name },
                onBack = {}
            )
        }
    }

    private fun typeGrams(description: String, grams: String) {
        composeRule.onNodeWithContentDescription(description).performTextInput(grams)
    }

    private fun addRow(name: String, grams: String) {
        composeRule.onNodeWithText("Add ingredient").performClick()
        composeRule.onAllNodes(nameField).onLast().performTextInput(name)
        typeGrams("Grams of $name", grams)
    }

    private fun saveAs(name: String) {
        composeRule.onNodeWithText("Save as recipe").performClick()
        composeRule.onNodeWithText("Name").performTextInput(name)
        composeRule.onNodeWithText("Save").performClick()
    }

    @Test
    fun startsEmptyWithBothButtonsDisabled() {
        composeRule.onNodeWithText("Convert recipe").assertExists()
        composeRule.onNodeWithText("Save as recipe").assertIsNotEnabled()
        composeRule.onNodeWithText("Use in calculator").assertIsNotEnabled()
        composeRule.onNodeWithText("Add more").assertIsOff()
    }

    @Test
    fun typedGramsShowTheConvertedWeights() {
        typeGrams("Flour", "500")
        typeGrams("Water", "325")
        composeRule.onNodeWithText("4 × 206 g").assertExists()
        composeRule.onNodeWithText("Use in calculator").assertIsEnabled()
    }

    @Test
    fun blankNameHidesTheTableAndDisablesTheButtons() {
        typeGrams("Flour", "500")
        composeRule.onNodeWithText("Add ingredient").performClick()
        composeRule.onNodeWithText("TOTAL DOUGH").assertDoesNotExist()
        composeRule.onNodeWithText("Save as recipe").assertIsNotEnabled()
        composeRule.onNodeWithText("Use in calculator").assertIsNotEnabled()
    }

    @Test
    fun useInCalculatorSendsTheRecipe() {
        typeGrams("Flour", "500")
        typeGrams("Water", "325")
        composeRule.onNodeWithText("Use in calculator").performClick()
        assertEquals(listOf(206), used.map { it.portionWeightGrams })
    }

    @Test
    fun savingOffersToAddNewNamesAndThenOpensTheRecipe() {
        typeGrams("Flour", "500")
        addRow("Malt", "5")
        composeRule.onNodeWithText("Save as recipe").performClick()
        composeRule.onNodeWithText("Add to saved ingredients").assertExists()
        composeRule.onAllNodesWithText("Malt").onLast().performClick()
        composeRule.onNodeWithText("Name").performTextInput("Book")
        composeRule.onNodeWithText("Save").performClick()
        assertEquals(listOf("Book"), saves.map { it.first })
        assertEquals(setOf("Malt"), saves.single().third)
        assertEquals(listOf("Book"), opened)
    }

    @Test
    fun savingWithAddMoreClearsAndConfirms() {
        typeGrams("Flour", "500")
        composeRule.onNodeWithText("Add more").performClick()
        saveAs("Book")
        assertEquals(emptyList<String>(), opened)
        composeRule.onNodeWithText("Saved \"Book\"").assertExists()
        assertEquals("", draft.flour)
    }

    @Test
    fun removingARowOffersUndo() {
        typeGrams("Flour", "500")
        addRow("Salt", "15")
        composeRule.onNodeWithContentDescription("Remove Salt").performClick()
        composeRule.onNodeWithContentDescription("Grams of Salt").assertDoesNotExist()
        composeRule.onNodeWithText("Removed \"Salt\"").assertExists()
        composeRule.onNodeWithText("Undo").performClick()
        composeRule.onNodeWithContentDescription("Grams of Salt").assertExists()
    }
}
