package com.example.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "system")

object SystemPreferences {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }
}