package com.example.data

import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    fun getMemos(): Flow<List<Memo>>
    fun insertMemo(content: String)
}