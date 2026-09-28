package com.example.pizza.dough

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Stored data must keep loading after the recipe model changes. */
class DoughDataFormatTest {
    private val storedJson = """
        {
          "currentRecipe": {
            "pizzaCount": 6, "ballWeightGrams": 280, "hydrationPercent": 65.0,
            "saltPercent": 3.0, "yeastPercent": 0.2, "oilPercent": 0.0
          },
          "savedRecipes": [
            {
              "id": "a1", "name": "Friday night",
              "recipe": {
                "pizzaCount": 4, "ballWeightGrams": 250, "hydrationPercent": 62.0,
                "saltPercent": 3.0, "yeastPercent": 0.2, "oilPercent": 0.0
              }
            }
          ]
        }
    """.trimIndent()

    @Test
    fun storedDataStillLoads() {
        val data = Json.decodeFromString(DoughData.serializer(), storedJson)
        assertEquals(6, data.currentRecipe.portionCount)
        assertEquals(280, data.currentRecipe.portionWeightGrams)
        val saved = data.savedRecipes.single().recipe
        assertEquals(4, saved.portionCount)
        assertEquals(250, saved.portionWeightGrams)
    }

    @Test
    fun recipesAreStoredWithTheSameKeys() {
        val json = Json.encodeToString(DoughRecipe.serializer(), DefaultDoughRecipe)
        assertTrue(json, json.contains("\"pizzaCount\":4"))
        assertTrue(json, json.contains("\"ballWeightGrams\":250"))
    }
}
