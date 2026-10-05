package com.example.pizza.dough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PercentageTest {
    private fun accepted(text: String) = (PercentRules.parse(text) as Parsed.Value).value

    @Test
    fun aValueOffItsStepIsRefused() {
        assertThrows(IllegalArgumentException::class.java) { Percentage(6250, 0) }
        assertThrows(IllegalArgumentException::class.java) { Percentage(305, 1) }
    }

    @Test
    fun roundedTextKeepsAtMostTheGivenDecimals() {
        assertEquals("62.6", Percentage(6255, 2).roundedText(maxDecimals = 1))
        assertEquals("62", Percentage(6200, 1).roundedText(maxDecimals = 1))
        assertEquals("0.25", Percentage(25, 2).roundedText(maxDecimals = 2))
    }

    @Test
    fun typedDecimalsSetThePrecision() {
        assertEquals(Percentage(hundredths = 6000, decimals = 0), accepted("60"))
        assertEquals(Percentage(hundredths = 70, decimals = 1), accepted("0.7"))
    }

    @Test
    fun trailingZerosCountAsDecimals() {
        assertEquals(Percentage(hundredths = 310, decimals = 2), accepted("3.10"))
    }

    @Test
    fun moreThanTwoDecimalsRoundHalfUp() {
        assertEquals(Percentage(hundredths = 13, decimals = 2), accepted("0.125"))
    }

    @Test
    fun commaCountsAsDecimalPoint() {
        assertEquals(Percentage(hundredths = 250, decimals = 1), accepted("2,5"))
    }

    @Test
    fun rangeEndsAreAccepted() {
        assertEquals(Percentage(hundredths = 0, decimals = 0), accepted("0"))
        assertEquals(Percentage(hundredths = 10000, decimals = 0), accepted("100"))
    }

    @Test
    fun anythingButANumberFrom0To100IsAnError() {
        listOf("", " ", "abc", ".", "120", "-1", "1e2", "100.01").forEach { text ->
            assertEquals(text, Parsed.Error("Enter 0 to 100"), PercentRules.parse(text))
        }
    }

    @Test
    fun steppingAddsOneStepWithoutDrift() {
        var value = Percentage(hundredths = 0, decimals = 1)
        repeat(3) { value = PercentRules.stepUp(value) }
        assertEquals(Percentage(hundredths = 30, decimals = 1), value)
    }

    @Test
    fun stepMatchesThePrecision() {
        assertEquals(Percentage(6100, 0), PercentRules.stepUp(Percentage(6000, 0)))
        assertEquals(Percentage(300, 1), PercentRules.stepDown(Percentage(310, 1)))
        assertEquals(Percentage(24, 2), PercentRules.stepDown(Percentage(25, 2)))
    }

    @Test
    fun steppingStaysWithin0To100AndKeepsTheDecimals() {
        assertEquals(Percentage(0, 2), PercentRules.stepDown(Percentage(1, 2)))
        assertEquals(Percentage(0, 2), PercentRules.stepDown(Percentage(0, 2)))
        assertEquals(Percentage(10000, 1), PercentRules.stepUp(Percentage(10000, 1)))
    }

    @Test
    fun buttonsAreDisabledAtTheRangeEnds() {
        assertFalse(PercentRules.canStepDown(Percentage(0, 2)))
        assertTrue(PercentRules.canStepUp(Percentage(0, 2)))
        assertFalse(PercentRules.canStepUp(Percentage(10000, 0)))
        assertTrue(PercentRules.canStepDown(Percentage(10000, 0)))
    }

    @Test
    fun fieldTextShowsTheDecimals() {
        assertEquals("0.00", PercentRules.fieldText(Percentage(0, 2)))
        assertEquals("3.10", PercentRules.fieldText(Percentage(310, 2)))
        assertEquals("62", PercentRules.fieldText(Percentage(6200, 0)))
        assertEquals("100", PercentRules.fieldText(Percentage(10000, 0)))
    }

    @Test
    fun tableTextDropsTrailingZeros() {
        assertEquals("0.2", Percentage(20, 2).tableText())
        assertEquals("62", Percentage(6200, 1).tableText())
        assertEquals("0", Percentage(0, 2).tableText())
        assertEquals("100", Percentage(10000, 2).tableText())
    }

    @Test
    fun hintShowsTheStep() {
        assertEquals("± 1", PercentRules.hintText(Percentage(6200, 0)))
        assertEquals("± 0.1", PercentRules.hintText(Percentage(70, 1)))
        assertEquals("± 0.01", PercentRules.hintText(Percentage(25, 2)))
        assertEquals("%", PercentRules.unit)
    }
}
