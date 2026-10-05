package com.example.pizza.dough

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResetMessageTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var dismissCount = 0

    private fun showCalculator(resetMessagePending: Boolean) {
        composeRule.setContent {
            DoughCalculatorContent(
                recipe = DefaultDoughRecipe,
                loadedRecipeName = null,
                resetMessagePending = resetMessagePending,
                onRecipeChange = {},
                onSaveRecipe = {},
                onUpdateLoadedRecipe = {},
                onOpenSavedRecipes = {},
                onOpenFullRecipe = {},
                onDismissResetMessage = { dismissCount++ }
            )
        }
    }

    @Test
    fun messageSaysWhatHappened() {
        showCalculator(resetMessagePending = true)
        composeRule.onNodeWithText("Saved recipes couldn't be read").assertExists()
        composeRule.onNodeWithText("had to start fresh", substring = true).assertExists()
        composeRule.onNodeWithText("copy of your old data was kept", substring = true)
            .assertExists()
    }

    @Test
    fun okDismissesTheMessageOnce() {
        showCalculator(resetMessagePending = true)
        composeRule.onNodeWithText("OK").performClick()
        assertEquals(1, dismissCount)
    }

    @Test
    fun backLeavesTheMessageOpen() {
        showCalculator(resetMessagePending = true)
        Espresso.pressBack()
        composeRule.onNodeWithText("Saved recipes couldn't be read").assertExists()
        assertEquals(0, dismissCount)
    }

    @Test
    fun noMessageWhenNothingWasReset() {
        showCalculator(resetMessagePending = false)
        composeRule.onNodeWithText("Saved recipes couldn't be read").assertDoesNotExist()
    }
}
