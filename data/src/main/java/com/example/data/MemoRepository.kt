package com.example.data

import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    fun getCategory(): Flow<List<Category>>
    fun getMemos(): Flow<List<Memo>>
    fun getMemosByCategory(categoryName: String?): Flow<List<Memo>>
    fun insertMemo(memo: Memo)
}