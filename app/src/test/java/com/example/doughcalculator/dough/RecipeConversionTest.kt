package com.example.doughcalculator.dough

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeConversionTest {
    private val empty = ConversionDraft(portionCount = 4)

    /** Adds a row named [name] holding [grams]. */
    private fun ConversionDraft.withRow(name: String, grams: String): ConversionDraft {
        val added = rows.add()
        val key = added.items.last().key
        return copy(rows = added.rename(key, name).changeGrams(key, grams))
    }

    private fun ConversionDraft.keyOf(name: String) = rows.items.first { it.name == name }.key

    private fun ConversionDraft.withRowGrams(name: String, grams: String) =
        copy(rows = rows.changeGrams(keyOf(name), grams))

    private fun ConversionDraft.withoutRow(name: String) = copy(rows = rows.remove(keyOf(name)))

    private fun ConversionDraft.ingredientNames() = toRecipe()?.ingredients?.map { it.name }

    private val bookRecipe = empty
        .copy(flour = "500", water = "325")
        .withRow("Salt", "15")
        .withRow("Yeast", "1")

    @Test
    fun bookRecipeBecomesPercentages() {
        val expected = DoughRecipe(
            portionCount = 4,
            portionWeightGrams = 210,
            hydration = Percentage(hundredths = 6500, decimals = 0),
            ingredients = listOf(
                Ingredient("Salt", Percentage(hundredths = 300, decimals = 0)),
                Ingredient("Yeast", Percentage(hundredths = 20, decimals = 1))
            )
        )
        assertEquals(expected, bookRecipe.toRecipe())
    }

    @Test
    fun percentagesRoundHalfUpToTwoDecimals() {
        val recipe = empty.copy(flour = "600").withRow("Yeast", "1").toRecipe()
        assertEquals(
            Percentage(hundredths = 17, decimals = 2),
            recipe?.ingredients?.single()?.percentage
        )
    }

    @Test
    fun commaCountsAsDecimalPoint() {
        val recipe = empty.copy(flour = "500").withRow("Yeast", "1,5").toRecipe()
        assertEquals(
            Percentage(hundredths = 30, decimals = 1),
            recipe?.ingredients?.single()?.percentage
        )
    }

    @Test
    fun anythingButGramsIsAnError() {
        listOf("abc", "1.255", "-1", "1e2", ".").forEach { text ->
            val draft = bookRecipe.withRowGrams("Salt", text)
            assertEquals(text, "Enter grams", draft.rowGramsError(draft.keyOf("Salt")))
            assertNull(text, draft.toRecipe())
        }
        assertEquals("Enter grams", empty.copy(water = "abc").waterError)
        assertEquals("Enter grams", empty.copy(flour = "abc").flourError)
    }

    @Test
    fun flourMustBeMoreThanZero() {
        val draft = bookRecipe.copy(flour = "0")
        assertEquals("More than 0 g", draft.flourError)
        assertNull(draft.toRecipe())
    }

    @Test
    fun blankFlourGivesNoRecipeAndNoMessage() {
        val draft = bookRecipe.copy(flour = "")
        assertNull(draft.flourError)
        assertNull(draft.toRecipe())
    }

    @Test
    fun blankWaterCountsAsZero() {
        val recipe = empty.copy(flour = "500").toRecipe()
        assertEquals(Percentage(hundredths = 0, decimals = 0), recipe?.hydration)
    }

    @Test
    fun overTwoHundredPercentIsRefused() {
        val draft = empty.copy(flour = "100", water = "250")
        assertEquals("Over 200% of the flour", draft.waterError)
        assertNull(draft.toRecipe())
        val exactlyTwoHundred = empty.copy(flour = "100", water = "200")
        assertNull(exactlyTwoHundred.waterError)
        assertNotNull(exactlyTwoHundred.toRecipe())
    }

    @Test
    fun portionWeightIsRoundedDown() {
        val draft = empty.copy(flour = "500", water = "325", portionCount = 4)
        assertEquals(206, draft.toRecipe()?.portionWeightGrams)
    }

    @Test
    fun portionsUnderFiveGramsAreRefused() {
        val draft = empty.copy(flour = "10", water = "8")
        assertEquals("Portions under 5 g", draft.portionsError)
        assertNull(draft.toRecipe())
        assertNull(draft.copy(portionCount = 3).portionsError)
    }

    @Test
    fun rowWithoutGramsIsLeftOut() {
        listOf("", "0").forEach { grams ->
            assertEquals(
                listOf("Salt", "Yeast"),
                bookRecipe.withRow("Honey", grams).ingredientNames()
            )
        }
    }

    @Test
    fun tinyAmountIsKeptAtZero() {
        val recipe = empty.copy(flour = "1000").withRow("Malt", "0.01").toRecipe()
        assertEquals(
            Ingredient("Malt", Percentage(hundredths = 0, decimals = 0)),
            recipe?.ingredients?.single()
        )
    }

    @Test
    fun blankNameBlocksWithoutMessage() {
        val draft = bookRecipe.copy(rows = bookRecipe.rows.add())
        assertEquals("", draft.rows.nameError(draft.rows.items.last().key))
        assertNull(draft.toRecipe())
    }

    @Test
    fun repeatedNameIsFlaggedOnTheLaterRow() {
        val draft = bookRecipe.withRow("salt", "1")
        assertNull(draft.rows.nameError(draft.keyOf("Salt")))
        assertEquals(
            "\"Salt\" is already in this recipe",
            draft.rows.nameError(draft.keyOf("salt"))
        )
        assertNull(draft.toRecipe())
    }

    @Test
    fun reservedNameIsRefused() {
        val draft = bookRecipe.withRow("Water", "10")
        assertEquals(
            "Flour and water are part of every recipe already",
            draft.rows.nameError(draft.keyOf("Water"))
        )
    }

    @Test
    fun namesAreTrimmed() {
        val draft = empty.copy(flour = "500").withRow("  Salt ", "15")
        assertEquals(listOf("Salt"), draft.ingredientNames())
    }

    @Test
    fun rowsKeepTheirOrder() {
        assertEquals(
            listOf("Salt", "Yeast", "Honey"),
            bookRecipe.withRow("Honey", "10").ingredientNames()
        )
    }

    @Test
    fun removingARowDropsIt() {
        assertEquals(listOf("Yeast"), bookRecipe.withoutRow("Salt").ingredientNames())
    }

    @Test
    fun undoPutsTheRowBackInPlace() {
        val draft = bookRecipe.withRow("Honey", "10")
        val removed = draft.withoutRow("Yeast")
        assertEquals("Yeast", removed.rows.lastRemoval?.row?.name)
        val restored = removed.rows.undoRemove()
        assertEquals(draft.rows.items, restored.items)
        assertNull(restored.lastRemoval)
    }

    @Test
    fun newerRemovalReplacesTheOlder() {
        val rows = bookRecipe.withoutRow("Salt").withoutRow("Yeast").rows.undoRemove()
        assertEquals(listOf("Yeast"), rows.items.map { it.name })
    }

    @Test
    fun clearRemovalEndsUndo() {
        val rows = bookRecipe.withoutRow("Salt").rows.clearRemoval()
        assertNull(rows.lastRemoval)
        assertEquals(listOf("Yeast"), rows.undoRemove().items.map { it.name })
    }

    @Test
    fun blankRowIsCalledNewIngredient() {
        assertEquals("new ingredient", bookRecipe.rows.add().items.last().displayName)
    }

    @Test
    fun clearingForTheNextRecipeKeepsPortionsAndTick() {
        val draft = bookRecipe
            .copy(portionCount = 6, addMore = true)
            .withoutRow("Salt")
            .clearedForNext()
        assertEquals("", draft.flour)
        assertEquals("", draft.water)
        assertTrue(draft.rows.items.isEmpty())
        assertEquals(6, draft.portionCount)
        assertTrue(draft.addMore)
        assertNull(draft.rows.lastRemoval)
    }

    @Test
    fun startsEmptyAndUnticked() {
        assertEquals("", empty.flour)
        assertEquals("", empty.water)
        assertTrue(empty.rows.items.isEmpty())
        assertFalse(empty.addMore)
        assertEquals(4, empty.portionCount)
    }

    @Test
    fun dropdownOffersOnlyNamesNotUsedByOtherRows() {
        val rows = bookRecipe.rows.add()
        val offered = rows.offeredNames(rows.items.last().key, listOf("Yeast", "Olive oil", "Salt"))
        assertEquals(listOf("Olive oil"), offered)
    }

    private val flourAndWater = empty.copy(flour = "500", water = "325")

    private fun percent(whole: Int) = Percentage(hundredths = whole * 100, decimals = 0)

    @Test
    fun previewKeepsShowingWithANewBlankRow() {
        val preview = flourAndWater.copy(rows = flourAndWater.rows.add()).preview()
        assertEquals(206, preview?.portionWeightGrams)
        assertEquals(percent(65), preview?.hydration)
        assertEquals(emptyList<Ingredient>(), preview?.ingredients)
    }

    @Test
    fun previewLeavesOutARowWithABlankName() {
        val preview = flourAndWater.withRow("", "15").preview()
        assertEquals(206, preview?.portionWeightGrams)
        assertEquals(emptyList<Ingredient>(), preview?.ingredients)
    }

    @Test
    fun previewLeavesOutARowWithAMessage() {
        val preview = empty.copy(flour = "100", water = "60").withRow("Salt", "250").preview()
        assertEquals(40, preview?.portionWeightGrams)
        assertEquals(percent(60), preview?.hydration)
        assertEquals(emptyList<Ingredient>(), preview?.ingredients)
    }

    @Test
    fun previewCountsUnusableWaterAsZero() {
        assertEquals(percent(0), empty.copy(flour = "500", water = "abc").preview()?.hydration)
    }

    @Test
    fun previewShowsPortionsUnderFiveGrams() {
        assertEquals(4, empty.copy(flour = "15", water = "3").preview()?.portionWeightGrams)
    }

    @Test
    fun previewNeedsUsableFlour() {
        listOf("", "0", "abc").forEach { flour ->
            assertNull(flour, flourAndWater.copy(flour = flour).preview())
        }
    }

    @Test
    fun previewOfACompleteRecipeIsTheRecipe() {
        assertEquals(bookRecipe.toRecipe(), bookRecipe.preview())
    }
}
