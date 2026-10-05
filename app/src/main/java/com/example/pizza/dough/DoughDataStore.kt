package com.example.pizza.dough

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Creates the DataStore for [file]. The app must keep only one per file. */
internal fun createDoughDataStore(
    file: File,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): DataStore<DoughData> = DataStoreFactory.create(
    serializer = DoughDataSerializer(),
    corruptionHandler = unreadableDataHandler(file),
    scope = scope,
    produceFile = { file }
)

/**
 * Keeps a copy of a data file that can't be read next to it, then starts fresh and flags the reset
 * so the user is told. DataStore calls this before replacing the file, so if the copy fails, the
 * exception stops the replacement and nothing is lost.
 */
internal fun unreadableDataHandler(file: File, clock: () -> Long = System::currentTimeMillis) =
    ReplaceFileCorruptionHandler {
        val copyName = "${file.nameWithoutExtension}-unreadable-${clock()}.${file.extension}"
        file.copyTo(File(file.parentFile, copyName))
        DoughData(resetMessagePending = true)
    }
