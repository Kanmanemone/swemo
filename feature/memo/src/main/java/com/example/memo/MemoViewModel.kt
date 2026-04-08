package com.example.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MemoRepository
import com.example.model.Category
import com.example.model.Memo
import com.example.model.MemoContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(private val repository: MemoRepository) : ViewModel() {

    private val categories: StateFlow<List<Category>> =
        repository
            .getCategory()
            .stateIn(
                scope = viewModelScope,
                started = SharingPolicy,
                initialValue = emptyList()
            )

    private val selectedCategoryId = MutableStateFlow<Long?>(null)

    private val selectedCategory: StateFlow<Category?> = combine(
        categories,
        selectedCategoryId,
        ::determineCategory
    ).stateIn(
        scope = viewModelScope,
        started = SharingPolicy,
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val memos: StateFlow<List<Memo>> =
        selectedCategory
            .map { category -> category?.id }
            .distinctUntilChanged()
            .flatMapLatest { categoryId ->
                if (categoryId == null) {
                    flowOf(emptyList())
                } else {
                    repository.getMemosByCategory(categoryId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingPolicy,
                initialValue = emptyList()
            )

    private val isEditorVisible = MutableStateFlow(false)

    private val editingMemo = MutableStateFlow<Memo?>(defaultEditingMemo())

    val uiState: StateFlow<MemoUiState> = combine(
        categories,
        selectedCategory,
        memos,
        isEditorVisible,
        editingMemo
    ) { categories, selectedCategory, memos, isEditorVisible, editingMemo ->
        MemoUiState(
            categories = categories,
            selectedCategory = selectedCategory,
            memos = memos,
            allLabels =
                memos.asSequence()
                    .flatMap { it.contents }
                    .map { it.label }
                    .toSet(),
            editorState =
                MemoUiState.EditorState(
                    isVisible = isEditorVisible,
                    editingMemo = editingMemo
                )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingPolicy,
        initialValue = MemoUiState()
    )

    fun selectCategory(categoryId: Long) {
        selectedCategoryId.value = categoryId
    }

    fun toggleEditorVisibility() {
        isEditorVisible.value = !isEditorVisible.value
    }

    fun addCategory(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        viewModelScope.launch {
            selectedCategoryId.value = repository.insertCategory(trimmedName)
        }
    }

    fun renameSelectedCategory(name: String) {
        val category = selectedCategory.value ?: return
        val trimmedName = name.trim()

        if (trimmedName.isBlank()) return
        if (trimmedName == category.name) return

        viewModelScope.launch {
            repository.updateCategoryName(category.id, trimmedName)
        }
    }

    fun deleteSelectedCategory() {
        val category = selectedCategory.value ?: return

        viewModelScope.launch {
            repository.deleteCategory(category.id)
        }
    }

    fun addMemoContent() {
        val currentEditingMemo = editingMemo.value ?: return
        editingMemo.value = currentEditingMemo.copy(
            contents = currentEditingMemo.contents + MemoContent(
                id = nextTemporaryId(currentEditingMemo),
                label = "label ${currentEditingMemo.contents.size + 1}",
                text = ""
            )
        )
    }

    fun updateEditingMemo(memo: Memo) {
        editingMemo.value = memo
    }

    fun addMemo() {
        val currentEditingMemo = editingMemo.value ?: return
        val currentSelectedCategoryId = selectedCategory.value?.id ?: return

        viewModelScope.launch {
            repository.insertMemo(currentEditingMemo.copy(categoryId = currentSelectedCategoryId))
            editingMemo.value = defaultEditingMemo()
        }
    }

    fun deleteMemo(memoId: Long) {
        viewModelScope.launch {
            repository.deleteMemo(memoId)
        }
    }
}

private val SharingPolicy = SharingStarted.WhileSubscribed(5_000)

private fun determineCategory(
    categories: List<Category>,
    selectedCategoryId: Long?,
): Category? =
    when {
        // 카테고리 리스트에 항목이 없음 → null
        categories.isEmpty() -> null
        // 선택된 카테고리가, null값임 → 첫 번째 카테고리 자동 선택
        selectedCategoryId == null -> categories.first()
        // 선택된 카테고리가, 리스트에 있음 → 그대로 유지
        else -> categories.firstOrNull { it.id == selectedCategoryId }
        // 선택된 카테고리가, (카테고리 삭제 등의 이유로) 리스트에 없음 → 첫 번째 카테고리 자동 선택
            ?: categories.first()
    }

private fun defaultEditingMemo(): Memo =
    Memo(
        categoryId = 0L,
        id = 0L,
        contents = listOf(MemoContent(id = 0L, label = "label 1", text = ""))
    )

private fun nextTemporaryId(memo: Memo): Long {
    val minId = memo.contents.minOfOrNull(MemoContent::id) ?: 0L
    return minOf(minId, 0L) - 1L
}