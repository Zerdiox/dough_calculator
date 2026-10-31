package com.example.doughcalculator.dough

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray

private const val SAVED_INGREDIENTS_KEY = "savedIngredients"

/**
 * An ingredient to pick when adding one to a recipe: its name and how it is measured, but no
 * percentage or amount, which belong to each recipe.
 */
@Serializable
data class SavedIngredient(
    val name: String,
    val isCount: Boolean = false,
    val gramsEachHundredths: Int? = null
) {
    /** The text a "Grams each" field starts with when this ingredient is picked. */
    val gramsEachText: String get() = gramsEachHundredths?.let(::gramsEachText).orEmpty()
}

/** How [this] ingredient of a recipe is saved. */
internal fun Ingredient.toSavedIngredient(): SavedIngredient = SavedIngredient(
    name = name,
    isCount = count != null,
    gramsEachHundredths = count?.gramsEachHundredths
)

/** Turns the stored list of names into saved ingredients, measured as percentages. */
internal val upgradeToSavedIngredients: FormatUpgrade = { data ->
    val names = data[SAVED_INGREDIENTS_KEY]?.jsonArray
    if (names == null) {
        data
    } else {
        val saved = names.map { JsonObject(mapOf("name" to it)) }
        JsonObject(data + (SAVED_INGREDIENTS_KEY to JsonArray(saved)))
    }
}
