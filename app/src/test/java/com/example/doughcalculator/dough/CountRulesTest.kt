package com.example.doughcalculator.dough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountRulesTest {
    private val error = Parsed.Error("Enter 0 to 100, in halves")

    @Test
    fun wholeAndHalfNumbersAreAccepted() {
        assertEquals(Parsed.Value(3), CountRules.parse("1.5"))
        assertEquals(Parsed.Value(3), CountRules.parse("1,5"))
        assertEquals(Parsed.Value(4), CountRules.parse("2"))
        assertEquals(Parsed.Value(4), CountRules.parse("2."))
        assertEquals(Parsed.Value(4), CountRules.parse("2.0"))
        assertEquals(Parsed.Value(1), CountRules.parse(".5"))
        assertEquals(Parsed.Value(0), CountRules.parse("0"))
        assertEquals(Parsed.Value(200), CountRules.parse("100"))
    }

    @Test
    fun anythingElseIsRefused() {
        listOf("1.3", "abc", "101", "100.5", "-1", "", "1.55").forEach {
            assertEquals(it, error, CountRules.parse(it))
        }
    }

    @Test
    fun stepsMoveAHalfAndStopAtTheEnds() {
        assertEquals(4, CountRules.stepUp(3))
        assertEquals(2, CountRules.stepDown(3))
        assertFalse(CountRules.canStepDown(0))
        assertTrue(CountRules.canStepDown(1))
        assertFalse(CountRules.canStepUp(200))
        assertTrue(CountRules.canStepUp(199))
    }

    @Test
    fun anAmountScaledPastTheMaximumStepsBackInsideIt() {
        assertFalse(CountRules.canStepUp(300))
        assertEquals(200, CountRules.stepDown(300))
    }

    @Test
    fun showsAmountsWithoutTrailingZeros() {
        assertEquals("1.5", CountRules.fieldText(3))
        assertEquals("2", CountRules.fieldText(4))
        assertEquals("0", CountRules.fieldText(0))
        assertEquals("100", CountRules.fieldText(200))
        assertEquals("± 0.5", CountRules.hintText(3))
    }
}
