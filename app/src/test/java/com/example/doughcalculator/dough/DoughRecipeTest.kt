package com.example.doughcalculator.dough

import org.junit.Assert.assertEquals
import org.junit.Test

class DoughRecipeTest {
    private val recipe = SaltAndYeastRecipe

    @Test
    fun ingredientsAddUpToTotalDoughWeight() {
        val amounts = recipe.amounts()
        val sum = amounts.flour + amounts.water + amounts.ingredients.sum()
        assertEquals(1000.0, sum, 0.01)
    }

    @Test
    fun flourIsTotalDividedBySumOfPercentages() {
        assertEquals(1000 / 1.682, recipe.amounts().flour, 0.01)
    }

    @Test
    fun ingredientsAreRelativeToFlour() {
        val amounts = recipe.amounts()
        assertEquals(amounts.flour * 0.65, amounts.water, 0.01)
        assertEquals(amounts.flour * 0.03, amounts.ingredients[0], 0.01)
        assertEquals(amounts.flour * 0.002, amounts.ingredients[1], 0.001)
    }

    @Test
    fun waterAboveOneHundredPercentStillAddsUp() {
        val wet = recipe.copy(
            hydration = Percentage(hundredths = 11000, decimals = 0),
            ingredients = emptyList()
        )
        val amounts = wet.amounts()
        assertEquals(1000 / 2.1, amounts.flour, 0.01)
        assertEquals(1000.0, amounts.flour + amounts.water, 0.01)
    }

    @Test
    fun zeroPercentLeavesIngredientOut() {
        val withoutWater = recipe.copy(hydration = Percentage(hundredths = 0, decimals = 0))
        assertEquals(0.0, withoutWater.amounts().water, 0.0)
    }

    @Test
    fun zeroPortionsNeedNoDough() {
        val amounts = recipe.copy(portionCount = 0).amounts()
        assertEquals(0.0, amounts.flour, 0.0)
        assertEquals(0.0, amounts.water, 0.0)
    }

    @Test
    fun morePortionsScaleEveryIngredient() {
        val amounts = recipe.amounts()
        val doubled = recipe.copy(portionCount = 8).amounts()
        assertEquals(amounts.flour * 2, doubled.flour, 0.01)
        assertEquals(amounts.water * 2, doubled.water, 0.01)
        amounts.ingredients.zip(doubled.ingredients).forEach { (single, double) ->
            assertEquals(single * 2, double, 0.001)
        }
    }

    private fun countRecipe(count: IngredientCount, portions: Int = 4, portionWeight: Int = 250) =
        DoughRecipe(
            portionCount = portions,
            portionWeightGrams = portionWeight,
            hydration = Percentage(hundredths = 6000, decimals = 0),
            ingredients = listOf(Ingredient("Eggs", Percentage(0, 0), count))
        )

    @Test
    fun countFollowsThePortionsToTheNearestHalf() {
        val eggs = IngredientCount(halves = 4, forPortions = 4)
        assertEquals(listOf(1, 2, 3, 4, 6), listOf(1, 2, 3, 4, 6).map { eggs.halvesFor(it) })
        // 2.5 eggs is exact at 5 portions; 7 portions rounds 3.5 exactly too.
        assertEquals(5, eggs.halvesFor(5))
        // 1 for 3 at 2 portions is 0.67, nearest half 0.5; at 4 portions 1.33, nearest 1.5.
        val one = IngredientCount(halves = 2, forPortions = 3)
        assertEquals(1, one.halvesFor(2))
        assertEquals(3, one.halvesFor(4))
    }

    @Test
    fun countRoundsHalfUp() {
        // 1 for 4 at 1 portion is exactly a quarter, half way between 0 and 0.5.
        assertEquals(1, IngredientCount(halves = 2, forPortions = 4).halvesFor(1))
        // 3 for 4 at 1 portion is 0.75, half way between 0.5 and 1.
        assertEquals(2, IngredientCount(halves = 6, forPortions = 4).halvesFor(1))
    }

    @Test
    fun countNeverScalesBelowAHalf() {
        assertEquals(1, IngredientCount(halves = 2, forPortions = 10).halvesFor(1))
    }

    @Test
    fun countAtZeroStaysZero() {
        assertEquals(0, IngredientCount(halves = 0, forPortions = 4).halvesFor(50))
    }

    @Test
    fun countWithGramsEachComesOutOfTheDough() {
        val amounts = countRecipe(IngredientCount(4, 4, gramsEachHundredths = 5000)).amounts()
        assertEquals(562.5, amounts.flour, 0.001)
        assertEquals(337.5, amounts.water, 0.001)
        assertEquals(100.0, amounts.ingredients.single(), 0.001)
        assertEquals(1000.0, amounts.flour + amounts.water + amounts.ingredients.sum(), 0.001)
    }

    @Test
    fun countWeightUsesTheRoundedAmount() {
        // 1 for 4 at 3 portions is 0.75, rounds half up to 1: 50 g.
        val recipe = countRecipe(IngredientCount(2, 4, gramsEachHundredths = 5000), portions = 3)
        assertEquals(50.0, recipe.amounts().ingredients.single(), 0.001)
    }

    @Test
    fun countWithoutGramsEachChangesNoWeight() {
        val amounts = countRecipe(IngredientCount(4, 4)).amounts()
        assertEquals(625.0, amounts.flour, 0.001)
        assertEquals(375.0, amounts.water, 0.001)
    }

    @Test
    fun countsAsHeavyAsTheDoughLeaveNoFlour() {
        val recipe = countRecipe(
            IngredientCount(8, 4, gramsEachHundredths = 5000),
            portionWeight = 25
        )
        assertEquals(0.0, recipe.amounts().flour, 0.0)
        assertEquals(0.0, recipe.amounts().water, 0.0)
        assertEquals(true, recipe.countsUseUpDough)
    }

    @Test
    fun countsLighterThanTheDoughLeaveFlour() {
        val recipe = countRecipe(IngredientCount(4, 4, gramsEachHundredths = 5000))
        assertEquals(false, recipe.countsUseUpDough)
    }

    @Test
    fun defaultRecipeIsFourPortionsOfWaterOnly() {
        val expected = DoughRecipe(
            portionCount = 4,
            portionWeightGrams = 250,
            hydration = Percentage(hundredths = 6200, decimals = 0),
            ingredients = emptyList()
        )
        assertEquals(expected, DefaultDoughRecipe)
    }
}
