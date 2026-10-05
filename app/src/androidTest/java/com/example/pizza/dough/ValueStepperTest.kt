package com.example.pizza.dough

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ValueStepperTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var value by mutableStateOf(250)
    private val reported = mutableListOf<Int>()

    /**
     * Shows a stepper at [grams]. With [echo] off, reported values don't come back on their own;
     * the test sets [value] itself, like a caller whose state lags behind the typing.
     */
    private fun showStepper(grams: Int, echo: Boolean = true) {
        value = grams
        composeRule.setContent {
            ValueStepper(
                label = "Portion weight",
                value = value,
                rules = GramsRules,
                onValueChange = {
                    if (echo) value = it
                    reported += it
                }
            )
        }
    }

    private fun field() = composeRule.onNode(hasSetTextAction())

    @Test
    fun showsLabelFieldUnitAndHint() {
        showStepper(grams = 250)
        composeRule.onNodeWithText("Portion weight").assertExists()
        field().assert(hasText("250"))
        composeRule.onNodeWithText("g", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("± 5 g").assertExists()
    }

    @Test
    fun buttonsReportTheSteppedValue() {
        showStepper(grams = 252)
        composeRule.onNodeWithContentDescription("Increase Portion weight").performClick()
        composeRule.onNodeWithContentDescription("Decrease Portion weight").performClick()
        assertEquals(listOf(255, 250), reported)
    }

    @Test
    fun decreaseIsDisabledAtTheBottom() {
        showStepper(grams = 5)
        composeRule.onNodeWithContentDescription("Decrease Portion weight").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Increase Portion weight").assertIsEnabled()
    }

    @Test
    fun increaseIsDisabledAtTheTop() {
        showStepper(grams = 999_999)
        composeRule.onNodeWithContentDescription("Increase Portion weight").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Decrease Portion weight").assertIsEnabled()
    }

    @Test
    fun typingAValidValueReportsIt() {
        showStepper(grams = 250)
        field().performTextReplacement("1200")
        assertEquals(listOf(1200), reported)
        field().assert(hasText("1200"))
    }

    @Test
    fun typingAnInvalidValueShowsTheErrorAndReportsNothing() {
        showStepper(grams = 250)
        field().performTextReplacement("3")
        composeRule.onNodeWithText("At least 5 g").assertExists()
        assertEquals(emptyList<Int>(), reported)
    }

    @Test
    fun leavingAfterAnInvalidValueShowsTheLastAcceptedValue() {
        showStepper(grams = 250)
        field().performTextReplacement("3")
        field().performImeAction()
        field().assert(hasText("250"))
        composeRule.onNodeWithText("At least 5 g").assertDoesNotExist()
    }

    @Test
    fun aValueChangedFromOutsideUpdatesTheField() {
        showStepper(grams = 250)
        composeRule.runOnIdle { value = 300 }
        field().assert(hasText("300"))
        composeRule.onNodeWithText("± 5 g").assertExists()
    }

    @Test
    fun typingAheadOfTheValueKeepsEveryCharacter() {
        showStepper(grams = 250, echo = false)
        field().performTextClearance()
        field().performTextInput("1")
        field().performTextInput("2")
        field().performTextInput("0")
        // The value catches up one keystroke at a time.
        composeRule.runOnIdle { value = 12 }
        field().assert(hasText("120"))
        composeRule.runOnIdle { value = 120 }
        field().assert(hasText("120"))
        assertEquals(listOf(12, 120), reported)
    }

    @Test
    fun aSeventhDigitIsIgnored() {
        showStepper(grams = 250)
        field().performTextReplacement("123456")
        field().performTextInput("7")
        field().assert(hasText("123456"))
        field().performTextReplacement("1234567")
        field().assert(hasText("123456"))
        composeRule.onNodeWithText("At least 5 g").assertDoesNotExist()
        assertEquals(listOf(123456), reported)
    }

    @Test
    fun stepButtonsUpdateTheFieldWhileItHasFocus() {
        showStepper(grams = 250)
        field().performTextReplacement("252")
        composeRule.onNodeWithContentDescription("Increase Portion weight").performClick()
        field().assert(hasText("255"))
    }
}
