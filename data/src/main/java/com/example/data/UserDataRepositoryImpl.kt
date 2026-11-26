package com.example.data

import com.example.datastore.SwemoPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val swemoPreferencesDataSource: SwemoPreferencesDataSource,
) : UserDataRepository {

    override val lastSelectedCategoryId: Flow<Long?> =
        swemoPreferencesDataSource.lastSelectedCategoryId

    override suspend fun setLastSelectedCategoryId(categoryId: Long) =
        swemoPreferencesDataSource.setLastSelectedCategoryId(categoryId)
}
