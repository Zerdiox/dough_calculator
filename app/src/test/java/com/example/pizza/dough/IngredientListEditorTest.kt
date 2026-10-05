package com.example.pizza.dough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IngredientListEditorTest {
    private val salt = Ingredient("Salt", Percentage(hundredths = 300, decimals = 1))
    private val yeast = Ingredient("Yeast", Percentage(hundredths = 20, decimals = 2))
    private val honey = Ingredient("Honey", Percentage(hundredths = 250, decimals = 1))
    private val editor = IngredientListEditor(listOf(salt, yeast, honey))

    private val IngredientListEditor.names get() = rows.map { it.name }

    private fun IngredientListEditor.keyOf(name: String) = rows.first { it.name == name }.key

    @Test
    fun startsWithOneRowPerIngredient() {
        assertEquals(listOf("Salt", "Yeast", "Honey"), editor.names)
        assertEquals(
            listOf(salt, yeast, honey).map {
                it.percentage
            },
            editor.rows.map { it.percentage }
        )
        assertTrue(editor.rows.none { it.isAdded })
        assertEquals(3, editor.rows.map { it.key }.distinct().size)
        assertNull(editor.lastRemoval)
    }

    @Test
    fun renameChangesOnlyThatRow() {
        val renamed = editor.rename(editor.keyOf("Salt"), "Sea salt")
        assertEquals(listOf("Sea salt", "Yeast", "Honey"), renamed.names)
        assertEquals(salt.percentage, renamed.rows.first().percentage)
    }

    @Test
    fun moveTakesARowToAnotherPlace() {
        assertEquals(
            listOf("Yeast", "Honey", "Salt"),
            editor.move(fromIndex = 0, toIndex = 2).names
        )
        assertEquals(
            listOf("Honey", "Salt", "Yeast"),
            editor.move(fromIndex = 2, toIndex = 0).names
        )
    }

    @Test
    fun addAppendsAnEmptyRowAtZeroWithWholeSteps() {
        val added = editor.add().rows.last()
        assertEquals("", added.name)
        assertEquals(Percentage(hundredths = 0, decimals = 0), added.percentage)
        assertTrue(added.isAdded)
        assertEquals(4, editor.add().rows.map { it.key }.distinct().size)
    }

    @Test
    fun removeTakesTheRowOut() {
        assertEquals(listOf("Salt", "Honey"), editor.remove(editor.keyOf("Yeast")).names)
    }

    @Test
    fun undoPutsTheRowBackInItsPlace() {
        val restored = editor.remove(editor.keyOf("Yeast")).undoRemove()
        assertEquals(editor.rows, restored.rows)
        assertNull(restored.lastRemoval)
    }

    @Test
    fun aSecondRemoveReplacesTheUndo() {
        val restored = editor.remove(editor.keyOf("Yeast")).let { it.remove(it.keyOf("Salt")) }
            .undoRemove()
        assertEquals(listOf("Salt", "Honey"), restored.names)
        assertEquals(restored, restored.undoRemove())
    }

    @Test
    fun clearRemovalKeepsTheRowsAndEndsUndo() {
        val removed = editor.remove(editor.keyOf("Honey"))
        val cleared = removed.clearRemoval()
        assertNull(cleared.lastRemoval)
        assertEquals(removed.rows, cleared.rows)
        assertEquals(cleared, cleared.undoRemove())
    }

    @Test
    fun canApplyFollowsTheNames() {
        assertTrue(editor.canApply)
        assertFalse(editor.add().canApply)
    }

    @Test
    fun aBlankRowIsCalledNewIngredient() {
        val added = editor.add()
        assertEquals("new ingredient", added.rows.last().displayName)
        val renamed = editor.rename(editor.keyOf("Salt"), " Sea salt ")
        assertEquals("Sea salt", renamed.rows.first().displayName)
    }

    @Test
    fun keysStayUniqueWhenAddingAfterARemoveAndUndoing() {
        val restored = editor.remove(editor.keyOf("Honey")).add().undoRemove()
        assertEquals(restored.rows.size, restored.rows.map { it.key }.distinct().size)
    }

    @Test
    fun toIngredientsReturnsTheTrimmedListInOrder() {
        val edited = editor.rename(editor.keyOf("Salt"), " Sea salt ")
            .move(fromIndex = 1, toIndex = 0)
            .add()
            .let { it.rename(it.rows.last().key, "Malt") }
        assertEquals(
            listOf(
                yeast,
                salt.copy(name = "Sea salt"),
                honey,
                Ingredient("Malt", Percentage(0, 0))
            ),
            edited.toIngredients()
        )
    }

    @Test
    fun toIngredientsIsNullWhileANameIsBlank() {
        val added = editor.add()
        assertNull(added.toIngredients())
        assertEquals("", added.nameError(added.rows.last().key))
    }

    @Test
    fun aDuplicateIsFlaggedOnTheLaterRow() {
        val added = editor.add().let { it.rename(it.rows.last().key, "salt") }
        assertNull(added.toIngredients())
        assertNull(added.nameError(added.keyOf("Salt")))
        assertEquals("\"Salt\" is already in this recipe", added.nameError(added.keyOf("salt")))
    }

    @Test
    fun aReservedNameIsFlagged() {
        val renamed = editor.rename(editor.keyOf("Honey"), "Water")
        assertNull(renamed.toIngredients())
        assertEquals(
            "Flour and water are part of every recipe already",
            renamed.nameError(renamed.keyOf("Water"))
        )
    }

    @Test
    fun theDropdownOffersSavedNamesNotInTheDraftAlphabetically() {
        val added = editor.add()
        val key = added.rows.last().key
        assertEquals(
            listOf("malt", "Olive oil"),
            added.offeredNames(key, listOf("Yeast", "Olive oil", "SALT", "malt"))
        )
    }

    @Test
    fun theDropdownKeepsNamesContainingTheTypedTextIgnoringCase() {
        val typed = editor.add().let { it.rename(it.rows.last().key, "O") }
        assertEquals(
            listOf("Olive oil"),
            typed.offeredNames(typed.rows.last().key, listOf("Olive oil", "Malt", "Sugar"))
        )
    }
}
