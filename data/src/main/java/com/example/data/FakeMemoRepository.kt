package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeMemoRepository : MemoRepository {
    private val memosFlow = MutableStateFlow<List<Memo>>(
        listOf(
            Memo(content = "Fake memo 1"),
            Memo(content = "Fake memo 2"),
        )
    )

    override fun getMemos(): Flow<List<Memo>> {
        return memosFlow
    }

    override fun insertMemo(content: String) {
        memosFlow.update { current ->
            current + Memo(content = content)
        }
    }
}