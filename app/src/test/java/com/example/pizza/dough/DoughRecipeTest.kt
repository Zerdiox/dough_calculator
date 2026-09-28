package com.example.pizza.dough

import org.junit.Assert.assertEquals
import org.junit.Test

class DoughRecipeTest {
    private val recipe = DoughRecipe(
        portionCount = 4,
        portionWeightGrams = 250,
        hydrationPercent = 65f,
        saltPercent = 3f,
        yeastPercent = 0.2f,
        oilPercent = 0f
    )

    @Test
    fun ingredientsAddUpToTotalDoughWeight() {
        val amounts = recipe.amounts()
        val sum = amounts.flour + amounts.water + amounts.salt + amounts.yeast + amounts.oil
        assertEquals(1000f, sum, 0.01f)
    }

    @Test
    fun flourIsTotalDividedBySumOfPercentages() {
        assertEquals(1000f / 1.682f, recipe.amounts().flour, 0.01f)
    }

    @Test
    fun ingredientsAreRelativeToFlour() {
        val amounts = recipe.amounts()
        assertEquals(amounts.flour * 0.65f, amounts.water, 0.01f)
        assertEquals(amounts.flour * 0.03f, amounts.salt, 0.01f)
        assertEquals(amounts.flour * 0.002f, amounts.yeast, 0.001f)
    }

    @Test
    fun zeroPercentLeavesIngredientOut() {
        assertEquals(0f, recipe.amounts().oil, 0f)
    }

    @Test
    fun zeroPortionsNeedNoDough() {
        val amounts = recipe.copy(portionCount = 0).amounts()
        assertEquals(0f, amounts.flour, 0f)
        assertEquals(0f, amounts.water, 0f)
    }

    @Test
    fun morePortionsScaleEveryIngredient() {
        val doubled = recipe.copy(portionCount = 8).amounts()
        assertEquals(recipe.amounts().flour * 2, doubled.flour, 0.01f)
    }
}
