package com.example.doughcalculator.dough

/**
 * The rules behind a value the user can type or step with − and +, so one control can show
 * grams and percentages alike.
 */
interface ValueRules<T> {
    /** Shown after the field, such as "g" or "%". */
    val unit: String

    /** Whether a decimal point can be typed, so the field offers the matching keyboard. */
    val acceptsDecimals: Boolean

    /** The most characters the field takes, or null for no limit; longer edits are ignored. */
    val maxLength: Int?

    /** Reads what the user typed into a value, or the message explaining why it can't be used. */
    fun parse(text: String): Parsed<T>

    fun stepUp(value: T): T

    fun stepDown(value: T): T

    fun canStepUp(value: T): Boolean

    fun canStepDown(value: T): Boolean

    /** The value as the field shows it. */
    fun fieldText(value: T): String

    /** The step size shown under the field, such as "± 5 g". */
    fun hintText(value: T): String
}
