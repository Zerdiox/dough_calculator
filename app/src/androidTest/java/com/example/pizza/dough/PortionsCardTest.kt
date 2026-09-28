package com.example.pizza.dough

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PortionsCardTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun showCard(portionCount: Int) {
        composeRule.setContent {
            PortionsCard(
                recipe = DefaultDoughRecipe.copy(portionCount = portionCount),
                onRecipeChange = {}
            )
        }
    }

    @Test
    fun cardUsesPortionWording() {
        showCard(portionCount = 4)
        composeRule.onNodeWithText("Portions").assertExists()
        composeRule.onNodeWithText("Portion weight").assertExists()
    }

    @Test
    fun fewerIsDisabledAtOnePortion() {
        showCard(portionCount = 1)
        composeRule.onNodeWithContentDescription("Fewer portions").assertIsNotEnabled()
    }

    @Test
    fun fewerIsEnabledAtTwoPortions() {
        showCard(portionCount = 2)
        composeRule.onNodeWithContentDescription("Fewer portions").assertIsEnabled()
    }

    @Test
    fun moreIsDisabledAtFiftyPortions() {
        showCard(portionCount = 50)
        composeRule.onNodeWithContentDescription("More portions").assertIsNotEnabled()
    }
}
