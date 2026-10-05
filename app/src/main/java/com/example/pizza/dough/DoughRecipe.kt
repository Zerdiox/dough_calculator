package com.example.pizza.dough

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A dough recipe in baker's percentages: water and every other ingredient are expressed relative
 * to the flour weight, so flour is always 100% and isn't stored.
 */
@Serializable
data class DoughRecipe(
    // Stored under their original keys so recipes saved by earlier versions still load.
    @SerialName("pizzaCount") val portionCount: Int,
    @SerialName("ballWeightGrams") val portionWeightGrams: Int,
    val hydration: Percentage,
    /** The ingredients besides flour and water, in the order the user gave them. */
    val ingredients: List<Ingredient>
) {
    val totalDoughGrams: Int get() = portionCount * portionWeightGrams

    fun amounts(): DoughAmounts {
        val otherFraction = hydration.fraction + ingredients.sumOf { it.percentage.fraction }
        val flour = totalDoughGrams / (1 + otherFraction)
        return DoughAmounts(
            flour = flour,
            water = flour * hydration.fraction,
            ingredients = ingredients.map { flour * it.percentage.fraction }
        )
    }
}

/** An ingredient besides flour and water. Its name is unique within a recipe. */
@Serializable
data class Ingredient(val name: String, val percentage: Percentage)

/** Ingredient weights in grams; [ingredients] follow the recipe's ingredients. */
data class DoughAmounts(val flour: Double, val water: Double, val ingredients: List<Double>)

private val Percentage.fraction: Double get() = hundredths / FULL_PERCENT_HUNDREDTHS.toDouble()

val DefaultDoughRecipe = DoughRecipe(
    portionCount = 4,
    portionWeightGrams = 250,
    hydration = Percentage(hundredths = 6200, decimals = 0),
    ingredients = emptyList()
)
