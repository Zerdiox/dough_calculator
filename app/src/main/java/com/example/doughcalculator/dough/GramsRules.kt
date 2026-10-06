package com.example.doughcalculator.dough

private const val MIN_GRAMS = 5
private const val STEP_GRAMS = 5
private const val MAX_GRAMS = 999_999
private const val MAX_DIGITS = 6

/**
 * Whole grams of at least 5, up to six digits. Typed weights are kept as typed; the buttons move
 * to the next multiple of 5 so an off-grid weight snaps back onto it.
 */
object GramsRules : ValueRules<Int> {
    // Digits only, so a sign is refused rather than read as a number.
    private val WHOLE_GRAMS = Regex("""\d{1,$MAX_DIGITS}""")
    private val ERROR = Parsed.Error("At least $MIN_GRAMS g")

    override val unit = "g"

    override val acceptsDecimals = false

    override val maxLength = MAX_DIGITS

    override fun parse(text: String): Parsed<Int> {
        val grams = text.trim().takeIf(WHOLE_GRAMS::matches)?.toInt()
        if (grams == null || grams < MIN_GRAMS) return ERROR
        return Parsed.Value(grams)
    }

    override fun stepUp(value: Int): Int = nextUp(value).coerceAtMost(MAX_GRAMS)

    override fun stepDown(value: Int): Int =
        ((value - 1) / STEP_GRAMS * STEP_GRAMS).coerceAtLeast(MIN_GRAMS)

    override fun canStepUp(value: Int): Boolean = nextUp(value) <= MAX_GRAMS

    override fun canStepDown(value: Int): Boolean = value > MIN_GRAMS

    override fun fieldText(value: Int): String = value.toString()

    override fun hintText(value: Int): String = "± $STEP_GRAMS g"

    private fun nextUp(value: Int): Int = (value / STEP_GRAMS + 1) * STEP_GRAMS
}
