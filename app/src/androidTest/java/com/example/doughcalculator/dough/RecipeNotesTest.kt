package com.example.doughcalculator.dough

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedrejeb.richeditor.model.RichTextState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeNotesTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var notes by mutableStateOf("")

    private fun showField(initialNotes: String = "") {
        composeRule.setContent {
            RecipeNotesField(initialNotes = initialNotes, onNotesChange = { notes = it })
        }
    }

    private fun typeInNotes(vararg texts: String) {
        texts.forEach { composeRule.onNodeWithText("Notes").performTextInput(it) }
    }

    private fun assertReported(expected: String) {
        composeRule.runOnIdle { assertEquals(expected, notes) }
    }

    private fun showCalculator(recipe: DoughRecipe, editor: IngredientListEditor? = null) {
        composeRule.setContent {
            TestCalculatorContent(
                recipe = recipe,
                ingredientEditor = editor,
                onRecipeChange = { notes = it.notes }
            )
        }
    }

    private fun showFullRecipe(recipe: DoughRecipe) {
        composeRule.setContent {
            FullRecipeScreen(title = "Friday night", initialRecipe = recipe, onBack = {})
        }
    }

    private fun assertBelow(lower: String, upper: String) {
        composeRule.onNodeWithText(lower, substring = true).performScrollTo()
        val lowerTop = composeRule.onNodeWithText(lower, substring = true).getBoundsInRoot().top
        val upperBottom = composeRule.onNodeWithText(upper).getBoundsInRoot().bottom
        assertTrue("$lower is at $lowerTop, $upper ends at $upperBottom", lowerTop >= upperBottom)
    }

    /** Reading [notes] back shows the same text as the field, as when the app opens again. */
    private fun assertReadsBackAsTyped() {
        val typed = composeRule.onNodeWithText("Notes").fetchSemanticsNode()
            .config[SemanticsProperties.EditableText].text
        composeRule.runOnIdle {
            assertEquals(typed, RichTextState().setHtml(notes).annotatedString.text)
        }
    }

    @Test
    fun noBlankLineAppearsBetweenTextAndAList() {
        showField()
        typeInNotes("Flour\n", "- ", "Tipo 00", "\n", "Whole wheat", "\n", "\n", "1. ", "Mix")
        assertReadsBackAsTyped()
    }

    @Test
    fun aBlankLineTheUserTypedStays() {
        showField()
        typeInNotes("Flour\n", "\n", "Water")
        assertReadsBackAsTyped()
    }

    @Test
    fun symbolsReadBackAsTyped() {
        val text = "100 g * 2, 2*3*4, # of balls, <b>, a & b, tipo_00_flour, 50% - 60%"
        showField()
        typeInNotes(text)
        composeRule.runOnIdle {
            assertEquals(text, RichTextState().setHtml(notes).annotatedString.text)
        }
    }

    @Test
    fun typingReachesTheRecipe() {
        showField()
        typeInNotes("Tipo 00")
        assertReported("<p>Tipo 00</p>")
    }

    @Test
    fun clearingTheFieldLeavesNoNote() {
        showField(initialNotes = "<p>Tipo 00</p>")
        composeRule.onNodeWithText("Tipo 00").performTextClearance()
        assertReported("")
    }

    @Test
    fun typingADashStartsABulletList() {
        showField()
        typeInNotes("- ", "Tipo 00")
        assertReported("<ul><li>Tipo 00</li></ul>")
    }

    @Test
    fun typingANumberStartsANumberedList() {
        showField()
        typeInNotes("1. ", "Mix")
        assertReported("<ol><li>Mix</li></ol>")
    }

    @Test
    fun aStoredNoteShowsInTheFieldWithoutReportingAChange() {
        notes = "Unchanged"
        showField(initialNotes = "<p>Tipo 00</p>")
        composeRule.onNodeWithText("Tipo 00").assertExists()
        assertReported("Unchanged")
    }

    @Test
    fun noteSurvivesRotation() {
        val tester = StateRestorationTester(composeRule)
        tester.setContent { RecipeNotesField(initialNotes = "", onNotesChange = { notes = it }) }
        typeInNotes("- ", "Tipo 00")
        tester.emulateSavedInstanceStateRestore()
        composeRule.onNodeWithText("Tipo 00", substring = true).assertExists()
        assertReported("<ul><li>Tipo 00</li></ul>")
    }

    @Test
    fun notesAreBelowStartKneading() {
        showCalculator(DefaultDoughRecipe)
        assertBelow(lower = "Notes", upper = "Start kneading")
    }

    @Test
    fun notesCanChangeWhileEditingIngredients() {
        showCalculator(DefaultDoughRecipe, editor = IngredientListEditor(emptyList()))
        val field = composeRule.onNodeWithText("Notes").performScrollTo()
        field.assertIsEnabled()
        field.performTextInput("Tipo 00")
        assertReported("<p>Tipo 00</p>")
    }

    @Test
    fun fullRecipeShowsTheNoteUnderTheWeights() {
        showFullRecipe(DefaultDoughRecipe.copy(notes = "<ol><li>Mix</li><li>Rest</li></ol>"))
        assertBelow(lower = "Mix", upper = "Flour")
        composeRule.onNodeWithText("1.", substring = true).assertExists()
    }

    @Test
    fun fullRecipeNoteCannotBeEdited() {
        showFullRecipe(DefaultDoughRecipe.copy(notes = "<p>Tipo 00</p>"))
        composeRule.onNodeWithText("Tipo 00").assert(!hasSetTextAction())
    }

    @Test
    fun fullRecipeShowsNothingWithoutANote() {
        showFullRecipe(DefaultDoughRecipe)
        composeRule.onNodeWithText("Notes").assertDoesNotExist()
        assertBelow(lower = "The screen stays on", upper = "Flour")
    }

    @Test
    fun fullRecipeKeepsTheNoteWhenPortionsChange() {
        showFullRecipe(DefaultDoughRecipe.copy(notes = "<p>Tipo 00</p>"))
        composeRule.onNodeWithText("Tipo 00").assertExists()
        composeRule.onNodeWithContentDescription("More portions").performClick()
        composeRule.onNodeWithText("Tipo 00").assertExists()
    }
}
