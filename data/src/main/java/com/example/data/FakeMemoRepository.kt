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
            Category(id = "1", name = "category 1"),
            Category(id = "2", name = "category 2"),
            Category(id = "3", name = "category 3"),
        )
    )

    private val memosFlow: MutableStateFlow<List<Memo>> = MutableStateFlow(
        listOf(
            Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 1"))),
            Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "label 2", text = "Fake memo 2"))),
            Memo(categoryId = "2", id = "0", contents = listOf(MemoContent(label = "label 3", text = "Fake memo 3"))),
            Memo(
                categoryId = "2", id = "0", contents = listOf(
                    MemoContent(label = "label 4", text = "Fake memo 4-1"),
                    MemoContent(label = "label 5", text = "Fake memo 4-2")
                )
            ),
            Memo(
                categoryId = "3", id = "0", contents = listOf(
                    MemoContent(label = "label 6", text = "Fake memo 5-1"),
                    MemoContent(label = "label 7", text = "Fake memo 5-2"),
                    MemoContent(label = "label 8", text = "Fake memo 5-3")
                )
            ),
            Memo(
                categoryId = "3", id = "0", contents = listOf(
                    MemoContent(label = "label 9", text = "Fake memo 6-1"),
                    MemoContent(label = "label 1", text = "Fake memo 6-2"),
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

    override fun getMemosByCategory(categoryId: String): Flow<List<Memo>> {
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