package com.example.pizza.dough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IngredientNameTest {
    @Test
    fun blankNameIsRefusedWithoutAMessage() {
        assertEquals("", ingredientNameError("", emptyList()))
        assertEquals("", ingredientNameError("   ", emptyList()))
    }

    @Test
    fun nameIsTrimmed() {
        assertNull(ingredientNameError("  Honey  ", listOf("Salt")))
        assertEquals(
            "\"Salt\" is already in this recipe",
            ingredientNameError(" Salt ", listOf("Salt"))
        )
    }

    @Test
    fun duplicateInAnotherCaseIsRefused() {
        assertEquals(
            "\"Salt\" is already in this recipe",
            ingredientNameError("salt", listOf("Yeast", "Salt"))
        )
    }

    @Test
    fun flourWaterAndHydrationAreRefusedInAnyCase() {
        listOf("Flour", "water", "HYDRATION", " Water ").forEach { name ->
            assertEquals(
                name,
                "Flour and water are part of every recipe already",
                ingredientNameError(name, emptyList())
            )
        }
    }

    @Test
    fun nameOfAtMost30CharactersIsAccepted() {
        assertNull(ingredientNameError("a".repeat(30), emptyList()))
        assertEquals("", ingredientNameError("a".repeat(31), emptyList()))
    }

    @Test
    fun similarNamesAreFine() {
        assertNull(ingredientNameError("Salt after", listOf("Salt before")))
    }

    @Test
    fun sameNameIgnoresCaseAndOuterSpaces() {
        assertTrue(" olive OIL".isSameNameAs("Olive oil "))
        assertFalse("Olive oil".isSameNameAs("Olive"))
    }
}
