package com.example.data

import com.example.model.Category
import com.example.model.Memo
import com.example.model.MemoContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeMemoRepository : MemoRepository {
    private val categoriesFlow: MutableStateFlow<List<Category>> = MutableStateFlow(
        listOf(
            Category(id = 1L, name = "category 1"),
            Category(id = 2L, name = "category 2"),
            Category(id = 3L, name = "category 3"),
        )
    )

    private val memosFlow: MutableStateFlow<List<Memo>> = MutableStateFlow(
        listOf(
            Memo(categoryId = 1L, id = 0L, contents = listOf(MemoContent(id = 1L, label = "label 1", text = "Fake memo 1"))),
            Memo(categoryId = 1L, id = 0L, contents = listOf(MemoContent(id = 2L, label = "label 2", text = "Fake memo 2"))),
            Memo(categoryId = 2L, id = 0L, contents = listOf(MemoContent(id = 3L, label = "label 3", text = "Fake memo 3"))),
            Memo(
                categoryId = 2L, id = 0L, contents = listOf(
                    MemoContent(id = 4L, label = "label 4", text = "Fake memo 4-1"),
                    MemoContent(id = 5L, label = "label 5", text = "Fake memo 4-2")
                )
            ),
            Memo(
                categoryId = 3L, id = 0L, contents = listOf(
                    MemoContent(id = 6L, label = "label 6", text = "Fake memo 5-1"),
                    MemoContent(id = 7L, label = "label 7", text = "Fake memo 5-2"),
                    MemoContent(id = 8L, label = "label 8", text = "Fake memo 5-3")
                )
            ),
            Memo(
                categoryId = 3L, id = 0L, contents = listOf(
                    MemoContent(id = 9L, label = "label 9", text = "Fake memo 6-1"),
                    MemoContent(id = 10L, label = "label 1", text = "Fake memo 6-2"),
                )
            ),
        )
    )

    override fun getCategory(): Flow<List<Category>> {
        return categoriesFlow
    }

    override fun getMemos(): Flow<List<Memo>> {
        return memosFlow
    }

    override fun getMemosByCategory(categoryId: Long): Flow<List<Memo>> {
        return memosFlow.map { memos ->
            memos.filter { it.categoryId == categoryId }
        }
    }

    override fun insertMemo(memo: Memo) {
        memosFlow.update { current ->
            current + memo
        }
    }
}