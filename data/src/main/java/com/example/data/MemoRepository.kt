package com.example.data

import com.example.model.Category
import com.example.model.Memo
import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    fun getCategory(): Flow<List<Category>>
    fun getMemos(): Flow<List<Memo>>
    fun getMemosByCategory(categoryId: Long): Flow<List<Memo>>

    suspend fun insertCategory(name: String): Long
    suspend fun insertMemo(memo: Memo): Long

    suspend fun updateCategoryName(categoryId: Long, name: String)
    suspend fun updateMemo(memo: Memo)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun deleteMemo(memoId: Long)
    suspend fun deleteMemoContent(memoContentId: Long)
}