package com.example.doughcalculator.dough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GramsRulesTest {
    private val rules: ValueRules<Int> = GramsRules

    @Test
    fun typedWholeGramsAreKeptAsTyped() {
        assertEquals(Parsed.Value(252), rules.parse("252"))
        assertEquals(Parsed.Value(5), rules.parse("5"))
        assertEquals(Parsed.Value(999_999), rules.parse("999999"))
    }

    @Test
    fun anythingButWholeGramsFrom5IsAnError() {
        listOf("", " ", "4", "0", "abc", "2.5", "-5", "+5", "1000000").forEach { text ->
            assertEquals(text, Parsed.Error("At least 5 g"), rules.parse(text))
        }
    }

    @Test
    fun buttonsMoveToTheNextMultipleOf5() {
        assertEquals(255, rules.stepUp(252))
        assertEquals(250, rules.stepDown(252))
        assertEquals(255, rules.stepUp(250))
        assertEquals(245, rules.stepDown(250))
    }

    @Test
    fun minusIsDisabledAt5() {
        assertFalse(rules.canStepDown(5))
        assertTrue(rules.canStepDown(6))
        assertEquals(5, rules.stepDown(7))
    }

    @Test
    fun plusIsDisabledWhenTheNextStepNeedsSevenDigits() {
        assertTrue(rules.canStepUp(999_994))
        assertFalse(rules.canStepUp(999_995))
    }

    @Test
    fun fieldAndHintText() {
        assertEquals("252", rules.fieldText(252))
        assertEquals("± 5 g", rules.hintText(252))
        assertEquals("g", rules.unit)
    }
}
