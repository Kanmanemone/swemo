package com.example.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class SwemoPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>,
) {
    val lastSelectedCategoryId: Flow<Long?> = userPreferences.data
        .map { it[LAST_SELECTED_CATEGORY_ID] }

    suspend fun setLastSelectedCategoryId(categoryId: Long) {
        try {
            userPreferences.edit { it[LAST_SELECTED_CATEGORY_ID] = categoryId }
        } catch (ioException: IOException) {
            Log.e("SwemoPreferences", "Failed to update user preferences", ioException)
        }
    }
}

private val LAST_SELECTED_CATEGORY_ID = longPreferencesKey("last_selected_category_id")
