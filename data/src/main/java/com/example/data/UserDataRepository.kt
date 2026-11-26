package com.example.data

import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    /** 마지막으로 연 카테고리. 저장된 적이 없으면 null */
    val lastSelectedCategoryId: Flow<Long?>

    suspend fun setLastSelectedCategoryId(categoryId: Long)
}
