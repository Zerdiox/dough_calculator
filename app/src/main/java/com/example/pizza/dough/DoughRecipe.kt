package com.example.pizza.dough

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val PERCENT = 100f

/**
 * A dough recipe in baker's percentages: every ingredient is expressed
 * relative to the flour weight, so flour is always 100%.
 */
@Serializable
data class DoughRecipe(
    // Stored under their original keys so recipes saved by earlier versions still load.
    @SerialName("pizzaCount") val portionCount: Int,
    @SerialName("ballWeightGrams") val portionWeightGrams: Int,
    val hydrationPercent: Float,
    val saltPercent: Float,
    val yeastPercent: Float,
    val oilPercent: Float
) {
    val totalDoughGrams: Int get() = portionCount * portionWeightGrams

    fun amounts(): DoughAmounts {
        val otherPercent = hydrationPercent + saltPercent + yeastPercent + oilPercent
        val flour = totalDoughGrams / (1 + otherPercent / PERCENT)
        return DoughAmounts(
            flour = flour,
            water = flour * hydrationPercent / PERCENT,
            salt = flour * saltPercent / PERCENT,
            yeast = flour * yeastPercent / PERCENT,
            oil = flour * oilPercent / PERCENT
        )
    }
}

/** Ingredient weights in grams. */
data class DoughAmounts(
    val flour: Float,
    val water: Float,
    val salt: Float,
    val yeast: Float,
    val oil: Float
)

val DefaultDoughRecipe = DoughRecipe(
    portionCount = 4,
    portionWeightGrams = 250,
    hydrationPercent = 62f,
    saltPercent = 3f,
    yeastPercent = 0.2f,
    oilPercent = 0f
)
