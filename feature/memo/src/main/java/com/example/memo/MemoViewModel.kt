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
            .getCategories()
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
                    repository.getMemos(categoryId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingPolicy,
                initialValue = emptyList()
            )

    private val isEditorVisible = MutableStateFlow(false)

    private val editingMemo = MutableStateFlow<Memo?>(defaultEditingMemo())

    // states
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
                    editingMemo = editingMemo,
                    mode = editingMemo.asEditorMode(),
                    isClearAllEnabled = (editingMemo != null) && (editingMemo != defaultEditingMemo()),
                    isSubmitEnabled = editingMemo.hasContents()
                )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingPolicy,
        initialValue = MemoUiState()
    )

    // events - crud
    // Category - C
    fun addCategory(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        viewModelScope.launch {
            selectedCategoryId.value = repository.insertCategory(trimmedName)
        }
    }

    // Category - U
    fun renameCategory(categoryId: Long, name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        viewModelScope.launch {
            val category = repository.getCategory(categoryId) ?: return@launch
            if (trimmedName == category.name) return@launch
            repository.updateCategoryName(category.id, trimmedName)
        }
    }

    // Category - D
    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            val deletedCategoryId = repository.deleteCategory(categoryId)
            // 만약 MemoEditor에서 편집 중인 메모가 삭제한 카테고리에 속했다면, 해당 편집을 취소
            if (deletedCategoryId == editingMemo.value?.categoryId) {
                resetEditingMemo()
            }
        }
    }

    // Memo - C
    fun addMemo(categoryId: Long, memo: Memo) {
        viewModelScope.launch {
            repository.insertMemo(memo.copy(categoryId = categoryId))
            resetEditingMemo()
        }
    }

    // Memo - U
    fun updateMemo(memo: Memo) {
        viewModelScope.launch {
            repository.updateMemo(memo)
            resetEditingMemo()
        }
    }

    // Memo - D
    fun deleteMemo(memoId: Long) {
        viewModelScope.launch {
            val deletedMemoId = repository.deleteMemo(memoId)
            // 삭제한 메모가 만약 MemoEditor에서 편집 중인 메모였다면, 해당 편집을 취소
            if (deletedMemoId == editingMemo.value?.id) {
                resetEditingMemo()
            }
        }
    }

    // MemoContent - C
    fun addMemoContent() {
        val currentEditingMemo = editingMemo.value ?: return
        editingMemo.value = currentEditingMemo.copy(
            contents = currentEditingMemo.contents + MemoContent(
                id = nextTemporaryId(currentEditingMemo),
                label = nextMemoContentLabel(currentEditingMemo.contents),
                text = ""
            )
        )
    }

    // MemoContent - U
    fun updateMemoContentText(contentId: Long, text: String) {
        val currentEditingMemo = editingMemo.value ?: return
        editingMemo.value = currentEditingMemo.copy(
            contents = currentEditingMemo.contents.map { content ->
                if (content.id == contentId) {
                    content.copy(text = text)
                } else {
                    content
                }
            }
        )
    }

    // MemoContent - D
    fun removeMemoContent(contentId: Long) {
        val currentEditingMemo = editingMemo.value ?: return
        editingMemo.value = currentEditingMemo.copy(
            contents = currentEditingMemo.contents.filterNot { content ->
                content.id == contentId
            }
        )
    }

    // event - etc
    fun selectCategory(categoryId: Long) {
        selectedCategoryId.value = categoryId
    }

    fun copyMemoToEditor(memo: Memo) {
        editingMemo.value = memo
        isEditorVisible.value = true
    }

    fun toggleEditorVisibility() {
        isEditorVisible.value = !isEditorVisible.value
    }

    fun clearEditingMemo() {
        resetEditingMemo()
    }

    private fun resetEditingMemo() {
        editingMemo.value = defaultEditingMemo()
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

//  마지막 라벨이 label n이면 label n+1, 내용이 없거나 마지막 라벨이 그 형식이 아니면 label 1
private fun nextMemoContentLabel(contents: List<MemoContent>): String {
    val lastLabelNumber = contents
        .lastOrNull()
        ?.label
        ?.removePrefix("label ")
        ?.toIntOrNull()

    return "label ${if (lastLabelNumber != null) lastLabelNumber + 1 else 1}"
}

private fun Memo?.hasContents(): Boolean =
    this?.contents?.isNotEmpty() == true

// Memo.id가 1 이상이면 Database에 있는 Memo라는 의미이므로 Update 모드
private fun Memo?.asEditorMode(): EditorMode =
    if (this != null && id > 0L) EditorMode.Update else EditorMode.Insert