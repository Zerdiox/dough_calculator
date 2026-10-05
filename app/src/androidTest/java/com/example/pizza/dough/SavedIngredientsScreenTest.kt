package com.example.pizza.dough

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

    private var savedIngredients by mutableStateOf(listOf("Honey", "Salt", "Yeast"))
    private val deleted = mutableListOf<String>()
    private val restored = mutableListOf<String>()

    private fun showScreen() {
        composeRule.setContent {
            SavedIngredientsScreen(
                savedIngredients = savedIngredients,
                onDeleteIngredient = {
                    deleted += it
                    savedIngredients = savedIngredients - it
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
        savedIngredients = given
        showScreen()
        composeRule.onNodeWithText("Saved ingredients").assertExists()
        val tops = given.map { name ->
            composeRule.onNodeWithText(name).fetchSemanticsNode().boundsInRoot.top
        }
        assertEquals(tops.sorted(), tops)
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
        assertEquals(listOf("Salt"), restored)
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
        assertEquals(listOf("Honey"), restored)
    }
}
