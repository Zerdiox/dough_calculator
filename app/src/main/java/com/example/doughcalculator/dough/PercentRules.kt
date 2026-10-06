package com.example.doughcalculator.dough

import java.math.BigDecimal
import java.math.RoundingMode

/** Percentages from 0 to 200, stepped by the precision the user typed. */
object PercentRules : ValueRules<Percentage> {
    // Plain digits with an optional decimal part; rules out signs and exponents that
    // BigDecimal would otherwise accept.
    private val NUMBER = Regex("""\d+(\.\d*)?|\.\d+""")
    private val MAX = BigDecimal(MAX_PERCENT)
    private val ERROR = Parsed.Error("Enter 0 to $MAX_PERCENT")

    override val unit = "%"

    override val acceptsDecimals = true

    // Checked against the maximum instead, and extra decimals are rounded rather than refused.
    override val maxLength: Int? = null

    override fun parse(text: String): Parsed<Percentage> {
        // Some keyboards only offer a comma as the decimal point.
        val typed = text.trim().replace(',', '.').takeIf(NUMBER::matches)?.let(::BigDecimal)
        if (typed == null || typed > MAX) return ERROR
        val hundredths = typed.setScale(MAX_PERCENT_DECIMALS, RoundingMode.HALF_UP).unscaledValue()
        return Parsed.Value(
            Percentage(
                hundredths = hundredths.toInt(),
                decimals = typed.scale().coerceAtMost(MAX_PERCENT_DECIMALS)
            )
        )
    }

    override fun stepUp(value: Percentage): Percentage = value.copy(
        hundredths = (value.hundredths + value.stepHundredths).coerceAtMost(MAX_PERCENT_HUNDREDTHS)
    )

    override fun stepDown(value: Percentage): Percentage = value.copy(
        hundredths = (value.hundredths - value.stepHundredths).coerceAtLeast(0)
    )

    override fun canStepUp(value: Percentage): Boolean = value.hundredths < MAX_PERCENT_HUNDREDTHS

    override fun canStepDown(value: Percentage): Boolean = value.hundredths > 0

    override fun fieldText(value: Percentage): String = value.fieldText()

    override fun hintText(value: Percentage): String =
        "± " + BigDecimal.valueOf(value.stepHundredths.toLong(), MAX_PERCENT_DECIMALS)
            .stripTrailingZeros()
            .toPlainString()
}
