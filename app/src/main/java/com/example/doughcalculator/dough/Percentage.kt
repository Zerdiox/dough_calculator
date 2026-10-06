package com.example.doughcalculator.dough

import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.serialization.Serializable

/** The finest precision a percentage can have. */
internal const val MAX_PERCENT_DECIMALS = 2

/** 100% in hundredths. */
internal const val FULL_PERCENT_HUNDREDTHS = 10_000

/**
 * A baker's percentage in whole hundredths, so 2.5% is 250. Whole numbers keep stepping exact
 * (0.1 + 0.1 + 0.1 is 0.3), which floats can't.
 *
 * [decimals] is the precision the user typed (0, 1 or 2). It sets the step and how many decimals
 * the field shows, and stays the same while the value is stepped.
 */
@Serializable
data class Percentage(val hundredths: Int, val decimals: Int) {
    init {
        require(hundredths in 0..FULL_PERCENT_HUNDREDTHS) { "Percentage out of range: $hundredths" }
        require(decimals in 0..MAX_PERCENT_DECIMALS) { "Unsupported decimals: $decimals" }
        // Off its step, the field couldn't show the value with its decimals.
        require(hundredths % stepHundredths == 0) { "$hundredths is off the step of $decimals" }
    }

    /** The size of one step in hundredths: 100, 10 or 1. */
    val stepHundredths: Int get() = STEP_HUNDREDTHS[decimals]

    /** The value with exactly [decimals] decimals, such as "0.00" or "3.10". */
    fun fieldText(): String = asDecimal().setScale(decimals).toPlainString()

    /** The value with at most two decimals and no trailing zeros, such as "0.2" or "62". */
    fun tableText(): String = roundedText(MAX_PERCENT_DECIMALS)

    /** The value rounded half up to at most [maxDecimals] decimals, without trailing zeros. */
    fun roundedText(maxDecimals: Int): String = asDecimal()
        .setScale(maxDecimals, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()

    private fun asDecimal(): BigDecimal =
        BigDecimal.valueOf(hundredths.toLong(), MAX_PERCENT_DECIMALS)

    private companion object {
        val STEP_HUNDREDTHS = listOf(100, 10, 1)
    }
}
