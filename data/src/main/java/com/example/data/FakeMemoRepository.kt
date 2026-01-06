package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeMemoRepository : MemoRepository {
    private val categoriesFlow = MutableStateFlow<List<Category>>(
        listOf(
            Category(name = "category 1"),
            Category(name = "category 2"),
            Category(name = "category 3"),
        )
    )

    private val memosFlow = MutableStateFlow<List<Memo>>(
        listOf(
            Memo(content = "Fake memo 1 (category 1)", categoryName = "category 1"),
            Memo(content = "Fake memo 2 (category 1)", categoryName = "category 1"),
            Memo(content = "Fake memo 3 (category 2)", categoryName = "category 2"),
            Memo(content = "Fake memo 4 (category 2)", categoryName = "category 2"),
            Memo(content = "Fake memo 5 (category 3)", categoryName = "category 3"),
            Memo(content = "Fake memo 6 (category 3)", categoryName = "category 3"),
        )
    )

    override fun getCategory(): Flow<List<Category>> {
        return categoriesFlow
    }

    override fun getMemos(): Flow<List<Memo>> {
        return memosFlow
    }

    override fun getMemosByCategory(categoryName: String?): Flow<List<Memo>> {
        return memosFlow.map { memos ->
            if (categoryName == null) {
                memos
            } else {
                memos.filter { it.categoryName == categoryName }
            }
        }
    }

    override fun insertMemo(memo: Memo) {
        memosFlow.update { current ->
            current + Memo(content = memo.content, categoryName = memo.categoryName)
        }
    }
}