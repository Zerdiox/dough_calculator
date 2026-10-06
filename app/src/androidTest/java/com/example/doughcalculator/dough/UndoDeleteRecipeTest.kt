package com.example.doughcalculator.dough

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UndoDeleteRecipeTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val deleted = mutableListOf<SavedRecipe>()
    private val restored = mutableListOf<Pair<SavedRecipe, Int>>()

    @Before
    fun showSavedRecipes() {
        composeRule.setContent {
            var savedRecipes by remember { mutableStateOf(listOf(neapolitan, fridayNight, party)) }
            SavedRecipesScreen(
                savedRecipes = savedRecipes,
                onOpenRecipe = {},
                onEditRecipe = {},
                onDeleteRecipe = {
                    deleted += it
                    savedRecipes = savedRecipes - it
                },
                onRestoreRecipe = { savedRecipe, index -> restored += savedRecipe to index },
                onOpenSavedIngredients = {},
                onBack = {}
            )
        }
    }

    @Test
    fun confirmingShowsTheMessageWithUndo() {
        delete("Friday night")
        composeRule.onNodeWithText("Deleted \"Friday night\"").assertExists()
        composeRule.onNodeWithText("Undo").assertExists()
    }

    @Test
    fun undoRestoresTheRecipeInItsPlaceOnce() {
        delete("Friday night")
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(listOf(fridayNight to 1), restored)
    }

    @Test
    fun cancelDeletesNothingAndShowsNoMessage() {
        tapDelete("Friday night")
        composeRule.onNodeWithText("Cancel").performClick()
        assertEquals(emptyList<SavedRecipe>(), deleted)
        composeRule.onNodeWithText("Deleted", substring = true).assertDoesNotExist()
    }

    @Test
    fun aSecondDeleteReplacesTheMessage() {
        delete("Friday night")
        delete("Party")
        composeRule.onNodeWithText("Deleted \"Friday night\"").assertDoesNotExist()
        composeRule.onNodeWithText("Deleted \"Party\"").assertExists()
        composeRule.onNodeWithText("Undo").performClick()
        assertEquals(listOf(party to 1), restored)
    }

    @Test
    fun undoEndsAfterAboutTenSeconds() {
        delete("Friday night")
        composeRule.mainClock.advanceTimeBy(UNDO_TIMEOUT_MILLIS)
        composeRule.onNodeWithText("Deleted \"Friday night\"").assertDoesNotExist()
        assertEquals(emptyList<Pair<SavedRecipe, Int>>(), restored)
    }

    private fun delete(recipeName: String) {
        tapDelete(recipeName)
        composeRule.onNodeWithText("Delete").performClick()
    }

    private fun tapDelete(recipeName: String) {
        composeRule
            .onNode(hasContentDescription("Delete") and hasAnyAncestor(hasText(recipeName)))
            .performClick()
    }

    private companion object {
        // A little past the ten seconds a long message shows.
        const val UNDO_TIMEOUT_MILLIS = 11_000L

        val neapolitan = SavedRecipe(id = "n0", name = "Neapolitan", recipe = DefaultDoughRecipe)
        val fridayNight = SavedRecipe(
            id = "a1",
            name = "Friday night",
            recipe = DefaultDoughRecipe.copy(
                hydration = Percentage(hundredths = 6500, decimals = 0)
            )
        )
        val party = SavedRecipe(
            id = "b2",
            name = "Party",
            recipe = DefaultDoughRecipe.copy(portionCount = 10, portionWeightGrams = 300)
        )
    }
}
