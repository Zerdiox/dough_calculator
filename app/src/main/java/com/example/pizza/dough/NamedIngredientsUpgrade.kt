package com.example.pizza.dough

import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val OLD_HYDRATION_KEY = "hydrationPercent"
private const val CURRENT_RECIPE_KEY = "currentRecipe"

// Version 1's default recipe, which a calculator never changed showed without storing it.
private val VersionOneDefaultRecipe = JsonObject(
    mapOf(
        "pizzaCount" to JsonPrimitive(4),
        "ballWeightGrams" to JsonPrimitive(250),
        OLD_HYDRATION_KEY to JsonPrimitive(62.0),
        "saltPercent" to JsonPrimitive(3.0),
        "yeastPercent" to JsonPrimitive(0.2),
        "oilPercent" to JsonPrimitive(0.0)
    )
)

/** A fixed ingredient of version 1: its new name, its old key and the decimals it starts with. */
private data class FixedIngredient(val name: String, val key: String, val decimals: Int)

// In the order the weights table showed them.
private val FixedIngredients = listOf(
    FixedIngredient(name = "Salt", key = "saltPercent", decimals = 1),
    FixedIngredient(name = "Yeast", key = "yeastPercent", decimals = 2),
    FixedIngredient(name = "Olive oil", key = "oilPercent", decimals = 1)
)

/**
 * Version 1 → 2: in the calculator's recipe and every saved recipe, water's percentage becomes a
 * percentage with decimals, and salt, yeast and olive oil become named ingredients, leaving out
 * those at 0%. A calculator that was never changed gets version 1's default recipe, so it keeps
 * showing the same values. Writes the version 2 shape directly, so later changes to the recipe
 * classes can't change what this upgrade produces.
 */
internal val upgradeToNamedIngredients: FormatUpgrade = { data ->
    // Version 2's default has no salt or yeast, so the old default has to be written out to stay.
    val withCalculator = JsonObject(mapOf(CURRENT_RECIPE_KEY to VersionOneDefaultRecipe) + data)
    withCalculator.update(CURRENT_RECIPE_KEY) { it.jsonObject.withNamedIngredients() }
        .update("savedRecipes") { savedRecipes ->
            JsonArray(
                savedRecipes.jsonArray.map { savedRecipe ->
                    savedRecipe.jsonObject.update("recipe") { it.jsonObject.withNamedIngredients() }
                }
            )
        }
}

/**
 * Replaces the value under [key], or changes nothing when there is none (a default isn't stored).
 */
private fun JsonObject.update(key: String, transform: (JsonElement) -> JsonElement): JsonObject {
    val value = this[key] ?: return this
    return JsonObject(this + (key to transform(value)))
}

private fun JsonObject.withNamedIngredients(): JsonObject {
    val ingredients = FixedIngredients.mapNotNull { fixed ->
        val percent = oldPercent(fixed.key)
        if (percent.signum() == 0) return@mapNotNull null
        JsonObject(
            mapOf(
                "name" to JsonPrimitive(fixed.name),
                "percentage" to percentageJson(percent, fixed.decimals)
            )
        )
    }
    val hydration = percentageJson(oldPercent(OLD_HYDRATION_KEY), decimals = 0)
    val oldKeys = FixedIngredients.map { it.key } + OLD_HYDRATION_KEY
    return JsonObject(
        this - oldKeys.toSet() +
            mapOf("hydration" to hydration, "ingredients" to JsonArray(ingredients))
    )
}

/**
 * The old percentage under [key], rounded to hundredths to drop float noise such as 0.15000001.
 * A missing or non-numeric value throws, which marks the data as unreadable.
 */
private fun JsonObject.oldPercent(key: String): BigDecimal =
    BigDecimal(requireNotNull(this[key]) { "Missing $key" }.jsonPrimitive.content)
        .setScale(MAX_PERCENT_DECIMALS, RoundingMode.HALF_UP)

/**
 * [percent] in the stored percentage shape, with at least [decimals] decimals and more when the
 * value needs them (a hand-edited 62.5% water), so no value changes.
 */
private fun percentageJson(percent: BigDecimal, decimals: Int): JsonObject {
    val neededDecimals = percent.stripTrailingZeros().scale().coerceAtLeast(0)
    return JsonObject(
        mapOf(
            "hundredths" to JsonPrimitive(percent.movePointRight(MAX_PERCENT_DECIMALS)),
            "decimals" to JsonPrimitive(maxOf(decimals, neededDecimals))
        )
    )
}
