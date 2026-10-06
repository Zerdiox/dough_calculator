package com.example.doughcalculator.dough

/** What typed text turned into: a value to use, or the message to show under the field. */
sealed interface Parsed<out T> {
    data class Value<T>(val value: T) : Parsed<T>

    data class Error(val message: String) : Parsed<Nothing>
}
