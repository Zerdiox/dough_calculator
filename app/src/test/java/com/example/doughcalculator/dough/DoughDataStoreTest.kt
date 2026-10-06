package com.example.doughcalculator.dough

import androidx.datastore.core.CorruptionException
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Data that can't be read is copied aside before the app starts fresh. */
class DoughDataStoreTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dataFile: File by lazy {
        folder.newFile("dough.json").apply { writeText("broken") }
    }

    @After
    fun closeDataStore() {
        scope.cancel()
    }

    @Test
    fun handlerKeepsACopyAndStartsFresh() {
        val data = handleCorruption(clock = { 1234L })
        assertEquals(DoughData(resetMessagePending = true), data)
        assertEquals("broken", File(folder.root, "dough-unreadable-1234.json").readText())
    }

    @Test
    fun secondFailureKeepsBothCopies() {
        handleCorruption(clock = { 1L })
        dataFile.writeText("broken again")
        handleCorruption(clock = { 2L })
        assertEquals("broken", File(folder.root, "dough-unreadable-1.json").readText())
        assertEquals("broken again", File(folder.root, "dough-unreadable-2.json").readText())
    }

    @Test
    fun handlerFailsWhenNoCopyCanBeMade() {
        dataFile.delete()
        assertThrows(IOException::class.java) { handleCorruption() }
    }

    @Test
    fun unreadableFileIsCopiedAndReplaced() = runBlocking {
        val data = createDoughDataStore(dataFile, scope).data.first()
        assertEquals(DoughData(resetMessagePending = true), data)
        assertEquals(listOf("broken"), unreadableCopies().map { it.readText() })
        val rewritten = DoughDataSerializer().readFrom(dataFile.inputStream())
        assertEquals(DoughData(resetMessagePending = true), rewritten)
    }

    @Test
    fun simultaneousReadsMakeOneCopy() = runBlocking {
        val dataStore = createDoughDataStore(dataFile, scope)
        List(2) { async { dataStore.data.first() } }.awaitAll()
        assertEquals(1, unreadableCopies().size)
    }

    @Test
    fun readableFileIsNotCopied() = runBlocking {
        dataFile.outputStream().use { DoughDataSerializer().writeTo(DoughData(), it) }
        val data = createDoughDataStore(dataFile, scope).data.first()
        assertEquals(DoughData(), data)
        assertTrue(unreadableCopies().isEmpty())
    }

    private fun handleCorruption(clock: () -> Long = System::currentTimeMillis): DoughData =
        runBlocking {
            unreadableDataHandler(dataFile, clock)
                .handleCorruption(CorruptionException("Cannot read saved dough data"))
        }

    private fun unreadableCopies(): List<File> = folder.root.listFiles { file ->
        file.name.startsWith("dough-unreadable-")
    }.orEmpty().toList()
}
