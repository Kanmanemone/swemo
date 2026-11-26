package com.example.memo

import com.example.model.Category
import com.example.model.Memo

data class MemoUiState(
    val isLoading: Boolean = false, // DataBase 및 DataStore의 최초값을 불러오는 중인지의 여부
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val memos: List<Memo> = emptyList(),
    val allLabels: Set<String> = emptySet(),
    val editorState: EditorState = EditorState()
) {
    data class EditorState(
        val mode: EditorMode = EditorMode.Insert,
        val memoId: Long? = null,
    )
}

enum class EditorMode {
    Insert,
    Update,
}
