package com.example.doughcalculator.dough

import org.junit.Assert.assertEquals
import org.junit.Test

class DoughRecipeTest {
    private val recipe = DoughRecipe(
        portionCount = 4,
        portionWeightGrams = 250,
        hydration = Percentage(hundredths = 6500, decimals = 0),
        ingredients = listOf(
            Ingredient(name = "Salt", percentage = Percentage(hundredths = 300, decimals = 1)),
            Ingredient(name = "Yeast", percentage = Percentage(hundredths = 20, decimals = 2))
        )
    )

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
