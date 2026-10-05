package com.example.pizza.dough

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Turns the stored JSON of one format version into that of the next version. The object still
 * holds the old `version` key; the serializer writes the current version when the data is saved.
 */
internal typealias FormatUpgrade = (JsonObject) -> JsonObject

/** Upgrades in order: the first turns version 1 into version 2. */
internal val FormatUpgrades: List<FormatUpgrade> = listOf(upgradeToNamedIngredients)

/**
 * Reads and writes the stored data. The file records its format version, and data from an older
 * version is upgraded step by step before it is decoded.
 */
internal class DoughDataSerializer(private val upgrades: List<FormatUpgrade> = FormatUpgrades) :
    Serializer<DoughData> {
    private val json = Json { ignoreUnknownKeys = true }
    private val currentVersion = FIRST_VERSION + upgrades.size

    override val defaultValue = DoughData()

    override suspend fun readFrom(input: InputStream): DoughData = try {
        val stored = json.parseToJsonElement(input.readBytes().decodeToString()).jsonObject
        json.decodeFromJsonElement(DoughData.serializer(), upgrade(stored))
    } catch (error: IllegalArgumentException) {
        // Also catches SerializationException, which extends it.
        throw CorruptionException("Cannot read saved dough data", error)
    }

    override suspend fun writeTo(t: DoughData, output: OutputStream) {
        val data = json.encodeToJsonElement(DoughData.serializer(), t).jsonObject
        val stored = JsonObject(mapOf(VERSION_KEY to JsonPrimitive(currentVersion)) + data)
        output.write(json.encodeToString(JsonObject.serializer(), stored).encodeToByteArray())
    }

    private fun upgrade(stored: JsonObject): JsonObject {
        // Data written before versions were recorded has no version: that is the first one.
        val version = stored[VERSION_KEY]?.jsonPrimitive?.int ?: FIRST_VERSION
        require(version <= currentVersion) { "Stored by a newer app version ($version)" }
        return upgrades.drop(version - FIRST_VERSION).fold(stored) { data, step -> step(data) }
    }

    private companion object {
        const val VERSION_KEY = "version"
        const val FIRST_VERSION = 1
    }
}
