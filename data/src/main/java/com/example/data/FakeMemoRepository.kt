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
            Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "content", text = "Fake memo 1 (category 1)"))),
            Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "content", text = "Fake memo 2 (category 1)"))),
            Memo(categoryId = "2", id = "0", contents = listOf(MemoContent(label = "content", text = "Fake memo 3 (category 2)"))),
            Memo(categoryId = "2", id = "0", contents = listOf(MemoContent(label = "content", text = "Fake memo 4 (category 2)"))),
            Memo(categoryId = "3", id = "0", contents = listOf(MemoContent(label = "content", text = "Fake memo 5 (category 3)"))),
            Memo(categoryId = "3", id = "0", contents = listOf(MemoContent(label = "content", text = "Fake memo 6 (category 3)"))),
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