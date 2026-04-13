package com.example.data

import com.example.model.Category
import com.example.model.Memo
import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun getCategory(categoryId: Long): Category?
    fun getMemos(): Flow<List<Memo>>
    fun getMemos(categoryId: Long): Flow<List<Memo>>

    suspend fun insertCategory(name: String): Long
    suspend fun insertMemo(memo: Memo): Long

    suspend fun updateCategoryName(categoryId: Long, name: String)
    suspend fun updateMemo(memo: Memo)
    suspend fun deleteCategory(categoryId: Long): Long?
    suspend fun deleteMemo(memoId: Long): Long?
    suspend fun deleteMemoContent(memoContentId: Long)
}