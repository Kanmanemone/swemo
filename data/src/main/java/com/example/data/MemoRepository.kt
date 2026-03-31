package com.example.data

import com.example.model.Category
import com.example.model.Memo
import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    fun getCategory(): Flow<List<Category>>
    fun getMemos(): Flow<List<Memo>>
    fun getMemosByCategory(categoryId: Long): Flow<List<Memo>>
    fun insertMemo(memo: Memo)
}