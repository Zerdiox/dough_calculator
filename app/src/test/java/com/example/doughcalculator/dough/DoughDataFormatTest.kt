package com.example.doughcalculator.dough

import androidx.datastore.core.CorruptionException
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/** Stored data must keep loading after the recipe model changes. */
class DoughDataFormatTest {
    private val dropSavedRecipes: FormatUpgrade = { JsonObject(it - "savedRecipes") }

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
        val data = read(storedJson)
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

    @Test
    fun writtenDataRecordsTheFormatVersion() {
        val json = write(DoughData())
        assertTrue(json, json.contains("\"version\":2"))
    }

    @Test
    fun resetMessageIsNotWrittenUntilItIsPending() {
        val json = write(DoughData())
        assertTrue(json, !json.contains("resetMessagePending"))
    }

    @Test
    fun loadedRecipeIsNotWrittenWhileNoneIsLoaded() {
        val json = write(DoughData())
        assertTrue(json, !json.contains("loadedRecipeId"))
    }

    @Test
    fun storedDataLoadsWithNoLoadedRecipe() {
        assertNull(read(storedJson).loadedRecipeId)
    }

    @Test
    fun writtenDataReadsBackTheSame() {
        val data = read(storedJson)
        assertEquals(data, read(write(data)))
    }

    @Test
    fun percentagesUpToTheMaximumReadBackTheSame() {
        val recipe = DefaultDoughRecipe.copy(
            hydration = Percentage(hundredths = 10000, decimals = 0),
            ingredients = listOf(Ingredient("Malt", Percentage(hundredths = 20000, decimals = 0)))
        )
        val data = DoughData(currentRecipe = recipe)
        assertEquals(data, read(write(data)))
    }

    @Test
    fun newerVersionIsUnreadable() {
        assertUnreadable(storedJson.withVersion(3))
    }

    @Test
    fun malformedJsonIsUnreadable() {
        assertUnreadable("broken")
    }

    @Test
    fun jsonThatIsNotAnObjectIsUnreadable() {
        assertUnreadable("[]")
    }

    @Test
    fun missingFieldIsUnreadable() {
        assertUnreadable(storedJson.replace("\"pizzaCount\": 6, ", ""))
    }

    @Test
    fun wrongTypeIsUnreadable() {
        assertUnreadable(storedJson.replace("\"pizzaCount\": 6", "\"pizzaCount\": \"six\""))
    }

    @Test
    fun olderVersionIsUpgradedBeforeLoading() {
        val data = read(storedJson, upgrades = FormatUpgrades + dropSavedRecipes)
        assertEquals(emptyList<SavedRecipe>(), data.savedRecipes)
        assertEquals(6, data.currentRecipe.portionCount)
    }

    @Test
    fun currentVersionIsNotUpgradedAgain() {
        val upgrades = FormatUpgrades + dropSavedRecipes
        val current = write(read(storedJson), DoughDataSerializer(upgrades))
        assertEquals(1, read(current, upgrades).savedRecipes.size)
    }

    @Test
    fun upgradeWrittenDataRecordsTheNewVersion() {
        val serializer = DoughDataSerializer(upgrades = listOf { it })
        val json = write(DoughData(), serializer)
        assertTrue(json, json.contains("\"version\":2"))
    }

    @Test
    fun failingUpgradeIsUnreadable() {
        val failingUpgrade: FormatUpgrade = { throw IllegalArgumentException("bad shape") }
        assertUnreadable(storedJson, upgrades = listOf(failingUpgrade))
    }

    @Test
    fun upgradeTurnsFixedIngredientsIntoNamedOnes() {
        val data = read(storedJson)
        val expected = DoughRecipe(
            portionCount = 4,
            portionWeightGrams = 250,
            hydration = Percentage(hundredths = 6200, decimals = 0),
            ingredients = listOf(
                Ingredient(name = "Salt", percentage = Percentage(hundredths = 300, decimals = 1)),
                Ingredient(name = "Yeast", percentage = Percentage(hundredths = 20, decimals = 2))
            )
        )
        assertEquals(expected, data.savedRecipes.single().recipe)
        assertEquals(
            expected.copy(
                portionCount = 6,
                portionWeightGrams = 280,
                hydration = Percentage(hundredths = 6500, decimals = 0)
            ),
            data.currentRecipe
        )
    }

    @Test
    fun upgradeKeepsOliveOilAboveZeroAfterYeast() {
        val recipe = read(storedJson.replace("\"oilPercent\": 0.0", "\"oilPercent\": 4.3"))
            .currentRecipe
        assertEquals(listOf("Salt", "Yeast", "Olive oil"), recipe.ingredients.map { it.name })
        assertEquals(
            Percentage(hundredths = 430, decimals = 1),
            recipe.ingredients.last().percentage
        )
    }

    @Test
    fun upgradeAddsDecimalsAValueNeeds() {
        val json = storedJson.replace("\"hydrationPercent\": 65.0", "\"hydrationPercent\": 62.5")
        assertEquals(
            Percentage(hundredths = 6250, decimals = 1),
            read(json).currentRecipe.hydration
        )
    }

    @Test
    fun upgradeDropsTheOldSlidersFloatNoise() {
        val json = storedJson
            .replace("\"saltPercent\": 3.0", "\"saltPercent\": 3.1000001")
            .replace("\"yeastPercent\": 0.2", "\"yeastPercent\": 0.15000001")
        assertEquals(
            listOf(Percentage(hundredths = 310, decimals = 1), Percentage(15, 2)),
            read(json).currentRecipe.ingredients.map { it.percentage }
        )
    }

    @Test
    fun aPercentageOffItsStepIsUnreadable() {
        // The default recipe isn't written, so the calculator holds a changed one.
        val data = DoughData(currentRecipe = DefaultDoughRecipe.copy(portionCount = 5))
        val json = write(data).replace(
            "\"hundredths\":6200,\"decimals\":0",
            "\"hundredths\":6250,\"decimals\":0"
        )
        assertTrue(json, json.contains("6250"))
        assertUnreadable(json)
    }

    @Test
    fun upgradeKeepsTheWeightsTable() {
        // As version 1 showed them.
        assertEquals(
            listOf("Flour 100% 999 g", "Water 65% 649 g", "Salt 3% 30 g", "Yeast 0.2% 2 g"),
            tableText(read(storedJson).currentRecipe)
        )
        assertEquals(
            listOf("Flour 100% 605 g", "Water 62% 375 g", "Salt 3% 18 g", "Yeast 0.2% 1.2 g"),
            tableText(read(storedJson).savedRecipes.single().recipe)
        )
        val withOil = storedJson.replace("\"oilPercent\": 0.0", "\"oilPercent\": 4.3")
        assertEquals(
            listOf(
                "Flour 100% 590 g",
                "Water 62% 366 g",
                "Salt 3% 18 g",
                "Yeast 0.2% 1.2 g",
                "Olive oil 4.3% 25 g"
            ),
            tableText(read(withOil).savedRecipes.single().recipe)
        )
    }

    @Test
    fun upgradeKeepsWhatAnUnchangedCalculatorShowed() {
        // Defaults aren't written, so a calculator never changed has no stored recipe.
        val json = """{ "savedRecipes": [] }"""
        assertEquals(
            listOf("Flour 100% 605 g", "Water 62% 375 g", "Salt 3% 18 g", "Yeast 0.2% 1.2 g"),
            tableText(read(json).currentRecipe)
        )
    }

    @Test
    fun upgradeOfARecipeWithoutSaltIsUnreadable() {
        assertUnreadable(storedJson.replace("\"saltPercent\": 3.0, ", ""))
    }

    @Test
    fun dataWithoutSavedIngredientsStartsWithTheDefaults() {
        assertEquals(listOf("Olive oil", "Salt", "Yeast"), read(storedJson).savedIngredients)
    }

    private fun tableText(recipe: DoughRecipe) =
        weightRows(recipe).map { "${it.name} ${it.percentText} ${it.gramsText}" }

    private fun String.withVersion(version: Int) = replaceFirst("{", "{ \"version\": $version,")

    private fun read(json: String, upgrades: List<FormatUpgrade> = FormatUpgrades): DoughData =
        runBlocking { DoughDataSerializer(upgrades).readFrom(json.byteInputStream()) }

    private fun write(data: DoughData, serializer: DoughDataSerializer = DoughDataSerializer()) =
        ByteArrayOutputStream()
            .also { runBlocking { serializer.writeTo(data, it) } }
            .toString(Charsets.UTF_8)

    private fun assertUnreadable(json: String, upgrades: List<FormatUpgrade> = FormatUpgrades) {
        assertThrows(CorruptionException::class.java) { read(json, upgrades) }
    }
}
