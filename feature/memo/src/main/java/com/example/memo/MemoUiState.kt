package com.example.memo

import com.example.model.Category
import com.example.model.Memo

data class MemoUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val memos: List<Memo> = emptyList(),
    val allLabels: Set<String> = emptySet(),
    val editorState: EditorState = EditorState()
) {
    data class EditorState(
        val isVisible: Boolean = false,
        val editingMemo: Memo? = null,
        val mode: EditorMode = EditorMode.Insert,
        val isClearAllEnabled: Boolean = false
    )
}

enum class EditorMode {
    Insert,
    Update,
}