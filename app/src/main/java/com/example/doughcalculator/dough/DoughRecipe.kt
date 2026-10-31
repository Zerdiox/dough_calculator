package com.example.doughcalculator.dough

import java.math.BigDecimal
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
    val ingredients: List<Ingredient>,
    /** Free text as the note editor's HTML, which keeps lists and any typed symbol as they are. */
    val notes: String = ""
) {
    val totalDoughGrams: Int get() = portionCount * portionWeightGrams

    /** Whether count ingredients weigh at least the whole dough, leaving nothing for the rest. */
    val countsUseUpDough: Boolean get() = countedGrams > 0 && countedGrams >= totalDoughGrams

    private val countedGrams: Double
        get() = ingredients.sumOf { it.count?.gramsFor(portionCount) ?: 0.0 }

    fun amounts(): DoughAmounts {
        val otherFraction = hydration.fraction + ingredients.sumOf { it.percentage.fraction }
        val flour = (totalDoughGrams - countedGrams).coerceAtLeast(0.0) / (1 + otherFraction)
        return DoughAmounts(
            flour = flour,
            water = flour * hydration.fraction,
            ingredients = ingredients.map { ingredient ->
                ingredient.count?.let { it.gramsFor(portionCount) ?: 0.0 }
                    ?: (flour * ingredient.percentage.fraction)
            }
        )
    }
}

/**
 * An ingredient besides flour and water. Its name is unique within a recipe. With a [count] it is
 * counted rather than weighed as a share of the flour, and its [percentage] stays at 0%.
 */
@Serializable
data class Ingredient(
    val name: String,
    val percentage: Percentage,
    val count: IngredientCount? = null
)

/**
 * How many of an ingredient go in: [halves] of one, for a batch of [forPortions]. Scaling always
 * starts from these two, so going to other portions and back gives the same amount again.
 *
 * [gramsEachHundredths] is the weight of one in hundredths of a gram, or null when it isn't known,
 * and then the ingredient has no part in the dough's weight.
 */
@Serializable
data class IngredientCount(
    val halves: Int,
    val forPortions: Int,
    val gramsEachHundredths: Int? = null
) {
    init {
        require(halves in 0..MAX_COUNT_HALVES) { "Count out of range: $halves" }
        require(forPortions > 0) { "Count for no portions: $forPortions" }
    }

    /** The amount in halves for a batch of [portions], rounded half up, and never to nothing. */
    fun halvesFor(portions: Int): Int {
        if (halves == 0) return 0
        return ((2 * halves * portions + forPortions) / (2 * forPortions)).coerceAtLeast(1)
    }

    /** The weight of the amount for a batch of [portions], or null without a weight for one. */
    fun gramsFor(portions: Int): Double? = gramsEachHundredths?.let {
        halvesFor(portions) / HALVES_PER_ONE.toDouble() * it / HUNDREDTHS_PER_GRAM
    }
}

/** The most of a count ingredient that can be set: 100, in halves. */
internal const val MAX_COUNT_HALVES = 200

/** One of a count ingredient, in halves. */
internal const val HALVES_PER_ONE = 2

private const val HUNDREDTHS_PER_GRAM = 100.0
private const val COUNT_DECIMAL_FACTOR = 5L

/** [halves] as a number without trailing zeros, such as "1.5" or "2". */
internal fun countText(halves: Int): String = BigDecimal.valueOf(halves * COUNT_DECIMAL_FACTOR, 1)
    .stripTrailingZeros()
    .toPlainString()

/** Ingredient weights in grams; [ingredients] follow the recipe's ingredients. */
data class DoughAmounts(val flour: Double, val water: Double, val ingredients: List<Double>)

private val Percentage.fraction: Double get() = hundredths / FULL_PERCENT_HUNDREDTHS.toDouble()

val DefaultDoughRecipe = DoughRecipe(
    portionCount = 4,
    portionWeightGrams = 250,
    hydration = Percentage(hundredths = 6200, decimals = 0),
    ingredients = emptyList()
)
