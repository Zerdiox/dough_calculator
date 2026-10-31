package com.example.doughcalculator.dough

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
    fun doneKeepsANoteChangedWhileEditing() {
        var finished: DoughRecipe? = null
        val editing = IngredientEditing(onFinish = { finished = it })
        editing.start(listOf(salt))
        editing.finish(DefaultDoughRecipe.copy(notes = "Changed"))
        assertEquals("Changed", finished?.notes)
        assertEquals(listOf(salt), finished?.ingredients)
    }

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
    fun choosingCountMakesAZeroCount() {
        val ingredients = editor.setCount(editor.keyOf("Salt"), true).toIngredients()!!
        assertEquals(Percentage(0, 0), ingredients.first().percentage)
        assertEquals(0, ingredients.first().count?.halves)
    }

    @Test
    fun changingTheKindBackRestoresWhatItHeld() {
        val key = editor.keyOf("Salt")
        assertEquals(salt, editor.setCount(key, true).setCount(key, false).toIngredients()!![0])

        val eggs = Ingredient("Eggs", Percentage(0, 0), IngredientCount(4, 4, 5000))
        val counted = IngredientListEditor(listOf(eggs))
        val countKey = counted.keyOf("Eggs")
        assertEquals(
            listOf(eggs),
            counted.setCount(countKey, false).setCount(countKey, true).toIngredients()
        )
    }

    @Test
    fun aCountChangedToAPercentageStartsAtZero() {
        val eggs = Ingredient("Eggs", Percentage(0, 0), IngredientCount(4, 4))
        val counted = IngredientListEditor(listOf(eggs))
        assertEquals(
            listOf(Ingredient("Eggs", Percentage(0, 0))),
            counted.setCount(counted.keyOf("Eggs"), false).toIngredients()
        )
    }

    @Test
    fun gramsEachIsApplied() {
        val key = editor.keyOf("Salt")
        val typed = editor.setCount(key, true).changeGramsEach(key, "52,5")
        assertEquals(5250, typed.toIngredients()!!.first().count?.gramsEachHundredths)
    }

    @Test
    fun blankOrZeroGramsEachMeansNone() {
        val key = editor.keyOf("Salt")
        listOf("", "0", "0.00").forEach { text ->
            val typed = editor.setCount(key, true).changeGramsEach(key, text)
            assertNull(typed.toIngredients()!!.first().count?.gramsEachHundredths)
        }
    }

    @Test
    fun gramsEachThatCantBeUsedBlocksApplying() {
        val key = editor.keyOf("Salt")
        val typed = editor.setCount(key, true).changeGramsEach(key, "abc")
        assertEquals("Enter grams", typed.gramsEachError(key))
        assertFalse(typed.canApply)
    }

    @Test
    fun gramsEachIsIgnoredOnAPercentageRow() {
        val key = editor.keyOf("Salt")
        val typed = editor.setCount(key, true).changeGramsEach(key, "abc").setCount(key, false)
        assertNull(typed.gramsEachError(key))
        assertTrue(typed.canApply)
    }

    @Test
    fun undoRestoresCountAndGramsEach() {
        val eggs = Ingredient("Eggs", Percentage(0, 0), IngredientCount(4, 4, 5000))
        val counted = IngredientListEditor(listOf(eggs))
        val restored = counted.remove(counted.keyOf("Eggs")).undoRemove()
        assertEquals(listOf(eggs), restored.toIngredients())
    }

    @Test
    fun pickingASavedCountSetsItUp() {
        val added = editor.add()
        val key = added.rows.last().key
        val picked = added.pick(key, SavedIngredient("Eggs", isCount = true, 5000))
        val row = picked.rows.last()
        assertEquals("Eggs", row.name)
        assertTrue(row.isCount)
        assertEquals("50", row.gramsEach)
        assertEquals(
            IngredientCount(0, 1, 5000),
            picked.toIngredients()!!.last().count
        )
    }

    @Test
    fun pickingASavedPercentageSetsItToPercent() {
        val added = editor.add()
        val key = added.rows.last().key
        val picked = added.setCount(key, true).pick(key, SavedIngredient("Malt"))
        assertFalse(picked.rows.last().isCount)
        assertNull(picked.toIngredients()!!.last().count)
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
