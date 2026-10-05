package com.example.pizza

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import com.example.pizza.dough.DoughData
import com.example.pizza.dough.createDoughDataStore

class PizzaApplication : Application() {
    /** The app's only DataStore: DataStore fails when two instances open the same file. */
    val doughDataStore: DataStore<DoughData> by lazy {
        createDoughDataStore(dataStoreFile("dough.json"))
    }
}
