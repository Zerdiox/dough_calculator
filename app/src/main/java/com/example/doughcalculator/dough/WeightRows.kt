package com.example.doughcalculator.dough

import java.math.RoundingMode

private const val SMALL_AMOUNT_GRAMS = 10.0
private val ZERO_GRAMS_TEXT = formatGrams(0.0)

/** One row of the weights table. */
data class WeightRow(val name: String, val percentText: String, val grams: Double) {
    val gramsText: String get() = formatGrams(grams)
}

/**
 * The weights table's rows: Flour, Water, then [recipe]'s ingredients, leaving out rows whose
 * weight shows as 0 g.
 */
fun weightRows(recipe: DoughRecipe): List<WeightRow> {
    val amounts = recipe.amounts()
    val flour = WeightRow(name = "Flour", percentText = "100%", grams = amounts.flour)
    val water = WeightRow(
        name = "Water",
        percentText = "${recipe.hydration.tableText()}%",
        grams = amounts.water
    )
    val others = recipe.ingredients.zip(amounts.ingredients) { ingredient, grams ->
        WeightRow(
            name = ingredient.name,
            percentText = "${ingredient.percentage.tableText()}%",
            grams = grams
        )
    }
    return (listOf(flour, water) + others).filterNot { it.gramsText == ZERO_GRAMS_TEXT }
}

/**
 * Whole grams, or one decimal under 10 g where a gram matters: 594.5 → "595 g", 1.19 → "1.2 g".
 */
internal fun formatGrams(grams: Double): String {
    val decimals = if (grams < SMALL_AMOUNT_GRAMS) 1 else 0
    return "${grams.formatDecimals(decimals)} g"
}

/** Rounds half up to at most [decimals] decimals and drops trailing zeros: 2.50 → "2.5". */
private fun Double.formatDecimals(decimals: Int): String = toBigDecimal()
    .setScale(decimals, RoundingMode.HALF_UP)
    .stripTrailingZeros()
    .toPlainString()
