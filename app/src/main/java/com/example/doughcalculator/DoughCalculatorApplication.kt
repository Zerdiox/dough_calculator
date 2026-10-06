package com.example.doughcalculator

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import com.example.doughcalculator.dough.DoughData
import com.example.doughcalculator.dough.createDoughDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DoughCalculatorApplication : Application() {
    /** Lives as long as the app, so a write keeps going after the screen that started it closes. */
    val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /** The app's only DataStore: DataStore fails when two instances open the same file. */
    val doughDataStore: DataStore<DoughData> by lazy {
        createDoughDataStore(dataStoreFile("dough.json"), applicationScope)
    }
}
