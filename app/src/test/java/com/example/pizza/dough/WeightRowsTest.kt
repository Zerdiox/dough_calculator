package com.example.pizza.dough

import org.junit.Assert.assertEquals
import org.junit.Test

class WeightRowsTest {
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
    fun rowsAreFlourWaterThenTheIngredientsInOrder() {
        val names = weightRows(recipe).map { it.name }
        assertEquals(listOf("Flour", "Water", "Salt", "Yeast"), names)
    }

    @Test
    fun ingredientAtZeroIsLeftOut() {
        val withoutSalt = recipe.copy(
            ingredients = recipe.ingredients.map {
                if (it.name == "Salt") it.copy(percentage = Percentage(0, 1)) else it
            }
        )
        assertEquals(listOf("Flour", "Water", "Yeast"), weightRows(withoutSalt).map { it.name })
    }

    @Test
    fun waterAtZeroIsLeftOut() {
        val withoutWater = recipe.copy(hydration = Percentage(hundredths = 0, decimals = 0))
        assertEquals(listOf("Flour", "Salt", "Yeast"), weightRows(withoutWater).map { it.name })
    }

    @Test
    fun ingredientShownAsZeroGramsIsLeftOut() {
        // 20 g of dough: about 0.04 g of yeast, which would show as "0 g".
        val tiny = recipe.copy(
            portionCount = 4,
            portionWeightGrams = 5,
            ingredients = listOf(Ingredient(name = "Yeast", percentage = Percentage(24, 2)))
        )
        assertEquals(listOf("Flour", "Water"), weightRows(tiny).map { it.name })
    }

    @Test
    fun weightsShowWholeGramsFromTenGrams() {
        // Flour is about 594.53 g.
        assertEquals("595 g", weightRows(recipe).first().gramsText)
    }

    @Test
    fun weightsUnderTenGramsShowOneDecimal() {
        // Yeast is about 1.19 g.
        assertEquals("1.2 g", weightRows(recipe).last().gramsText)
    }

    @Test
    fun weightsRoundHalfUp() {
        assertEquals("595 g", row(grams = 594.5).gramsText)
        assertEquals("1.3 g", row(grams = 1.25).gramsText)
    }

    @Test
    fun percentagesDropTrailingZeros() {
        val rows = weightRows(
            recipe.copy(
                hydration = Percentage(hundredths = 6200, decimals = 0),
                ingredients = listOf(
                    Ingredient(name = "Salt", percentage = Percentage(310, 2)),
                    Ingredient(name = "Yeast", percentage = Percentage(25, 2))
                )
            )
        )
        assertEquals(listOf("100%", "62%", "3.1%", "0.25%"), rows.map { it.percentText })
    }

    private fun row(grams: Double) = WeightRow(name = "Salt", percentText = "3%", grams = grams)
}
