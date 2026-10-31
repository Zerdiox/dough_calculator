package com.example.doughcalculator.dough

import java.math.BigDecimal

/** A count ingredient's amount in halves, from 0 to 100, typed or stepped by a half. */
object CountRules : ValueRules<Int> {
    // A whole number, optionally ending in a half or a point still being typed.
    private val NUMBER = Regex("""\d{1,3}(\.[05]?)?|\.5""")
    private val MAX = BigDecimal(MAX_COUNT_HALVES / HALVES_PER_ONE)
    private val ERROR = Parsed.Error("Enter 0 to ${MAX_COUNT_HALVES / HALVES_PER_ONE}, in halves")

    override val unit = ""

    override val acceptsDecimals = true

    override val maxLength: Int? = null

    override fun parse(text: String): Parsed<Int> {
        // Some keyboards only offer a comma as the decimal point.
        val typed = text.trim().replace(',', '.').takeIf(NUMBER::matches)?.let(::BigDecimal)
        if (typed == null || typed > MAX) return ERROR
        return Parsed.Value((typed * BigDecimal(HALVES_PER_ONE)).intValueExact())
    }

    override fun stepUp(value: Int): Int = (value + 1).coerceAtMost(MAX_COUNT_HALVES)

    // A count scaled to more portions can show above the maximum.
    override fun stepDown(value: Int): Int = (value - 1).coerceIn(0, MAX_COUNT_HALVES)

    override fun canStepUp(value: Int): Boolean = value < MAX_COUNT_HALVES

    override fun canStepDown(value: Int): Boolean = value > 0

    override fun fieldText(value: Int): String = countText(value)

    override fun hintText(value: Int): String = "± 0.5"
}
