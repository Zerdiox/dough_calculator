package com.example.pizza.dough

import androidx.datastore.core.CorruptionException
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
        assertTrue(json, json.contains("\"version\":1"))
    }

    @Test
    fun resetMessageIsNotWrittenUntilItIsPending() {
        val json = write(DoughData())
        assertTrue(json, !json.contains("resetMessagePending"))
    }

    @Test
    fun writtenDataReadsBackTheSame() {
        val data = read(storedJson)
        assertEquals(data, read(write(data)))
    }

    @Test
    fun newerVersionIsUnreadable() {
        assertUnreadable(storedJson.withVersion(2))
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
        val dropSavedRecipes: FormatUpgrade = { JsonObject(it - "savedRecipes") }
        val data = read(storedJson, upgrades = listOf(dropSavedRecipes))
        assertEquals(emptyList<SavedRecipe>(), data.savedRecipes)
        assertEquals(6, data.currentRecipe.portionCount)
    }

    @Test
    fun currentVersionIsNotUpgradedAgain() {
        val dropSavedRecipes: FormatUpgrade = { JsonObject(it - "savedRecipes") }
        val data = read(storedJson.withVersion(2), upgrades = listOf(dropSavedRecipes))
        assertEquals(1, data.savedRecipes.size)
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
