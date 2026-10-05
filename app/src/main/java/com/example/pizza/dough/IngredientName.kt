package com.example.pizza.dough

/** The longest ingredient name, in characters. */
internal const val MAX_NAME_LENGTH = 30

// Flour and water are in every recipe, so these names would show them twice.
private val RESERVED_NAMES = listOf("Flour", "Water", "Hydration")

/**
 * Why [name] can't be used for an ingredient next to [otherNames], or null when it can.
 *
 * Leading and trailing spaces don't count. An empty message means the name is refused without
 * explanation: a blank name is plainly unfinished, and a name field can stop at 30 characters
 * instead of explaining the limit.
 */
fun ingredientNameError(name: String, otherNames: List<String>): String? {
    val trimmed = name.trim()
    val duplicate = otherNames.firstOrNull { it.isSameNameAs(trimmed) }
    return when {
        trimmed.isEmpty() || trimmed.length > MAX_NAME_LENGTH -> ""

        RESERVED_NAMES.any { it.isSameNameAs(trimmed) } ->
            "Flour and water are part of every recipe already"

        duplicate != null -> "\"${duplicate.trim()}\" is already in this recipe"

        else -> null
    }
}

/** Whether this and [other] name the same ingredient: case and outer spaces don't count. */
internal fun String.isSameNameAs(other: String): Boolean =
    trim().equals(other.trim(), ignoreCase = true)
