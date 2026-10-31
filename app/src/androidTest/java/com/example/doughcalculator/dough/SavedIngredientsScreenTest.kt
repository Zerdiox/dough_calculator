package com.example.doughcalculator.dough

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedIngredientsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var savedIngredients by mutableStateOf(saved("Honey", "Salt", "Yeast"))
    private val deleted = mutableListOf<String>()
    private val restored = mutableListOf<SavedIngredient>()

    private fun saved(vararg names: String) = names.map { SavedIngredient(it) }

    private fun showScreen() {
        composeRule.setContent {
            SavedIngredientsScreen(
                savedIngredients = savedIngredients,
                onDeleteIngredient = {
                    deleted += it
                    savedIngredients = savedIngredients.filterNot { saved -> saved.name == it }
                },
                onRestoreIngredient = { restored += it },
                onBack = {}
            )
        }
    }

    @Test
    fun showsTheTitleAndTheNamesAsGiven() {
        // Not alphabetical, so a list the screen sorted itself would show.
        val given = listOf("Yeast", "Honey", "Salt")
        savedIngredients = given.map { SavedIngredient(it) }
        showScreen()
        composeRule.onNodeWithText("Saved ingredients").assertExists()
        val tops = given.map { name ->
            composeRule.onNodeWithText(name).fetchSemanticsNode().boundsInRoot.top
        }
        assertEquals(tops.sorted(), tops)
    }

    @Test
    fun countsShowTheirSetupUnderTheName() {
        savedIngredients = listOf(
            SavedIngredient("Eggs", isCount = true, gramsEachHundredths = 5000),
            SavedIngredient("Cloves", isCount = true),
            SavedIngredient("Salt")
        )
        showScreen()
        composeRule.onNodeWithText("Count · 50 g each").assertExists()
        composeRule.onNodeWithText("Count").assertExists()
    }

    @Test
    fun undoGivesBackTheWholeCount() {
        val eggs = SavedIngredient("Eggs", isCount = true, gramsEachHundredths = 5000)
        savedIngredients = listOf(eggs)
        showScreen()
        composeRule.onNodeWithContentDescription("Delete Eggs").performClick()
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(listOf(eggs), restored)
    }

    @Test
    fun withNoneItSaysHowToAddOne() {
        savedIngredients = emptyList()
        showScreen()
        composeRule.onNodeWithText("No saved ingredients", substring = true).assertExists()
        composeRule.onNodeWithText("saving a recipe", substring = true).assertExists()
    }

    @Test
    fun deleteReportsTheNameAndOffersUndo() {
        showScreen()
        composeRule.onNodeWithContentDescription("Delete Salt").performClick()
        assertEquals(listOf("Salt"), deleted)
        composeRule.onNodeWithText("Deleted \"Salt\"").assertExists()
        composeRule.onNodeWithText("Undo").assertExists()
    }

    @Test
    fun undoReportsTheRestore() {
        showScreen()
        composeRule.onNodeWithContentDescription("Delete Salt").performClick()
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(saved("Salt"), restored)
    }

    @Test
    fun aSecondDeleteReplacesTheMessage() {
        showScreen()
        composeRule.onNodeWithContentDescription("Delete Salt").performClick()
        composeRule.onNodeWithContentDescription("Delete Honey").performClick()
        composeRule.onNodeWithText("Deleted \"Salt\"").assertDoesNotExist()
        composeRule.onNodeWithText("Deleted \"Honey\"").assertExists()
        composeRule.onAllNodesWithText("Undo").fetchSemanticsNodes().let {
            assertEquals(1, it.size)
        }
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(saved("Honey"), restored)
    }
}
