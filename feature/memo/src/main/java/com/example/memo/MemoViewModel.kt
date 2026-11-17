package com.example.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MemoRepository
import com.example.model.Category
import com.example.model.Memo
import androidx.compose.foundation.text.input.TextFieldState
import com.example.memo.components.bulletedListTextFieldState
import com.example.memo.components.setBulletedListText
import com.example.memo.components.toBulletedListText
import com.example.memo.components.toMemoContents
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

    // 저장할 때 어느 메모(id, categoryId)에 쓸지. id가 0이면 새 메모. 내용(contents)은 editorTextState가 원본이다
    private val editTarget = MutableStateFlow<Memo?>(defaultEditTarget())

    /**
     * MemoEditor 입력란의 상태. 편집 중인 메모 내용의 원본이다.
     *
     * - 저장할 때 이 텍스트를 한 번 MemoContent 목록으로 바꿔 [editTarget]에 담는다.
     */
    internal val editorTextState: TextFieldState = bulletedListTextFieldState()

    // states
    val uiState: StateFlow<MemoUiState> = combine(
        categories,
        selectedCategory,
        memos,
        isEditorVisible,
        editTarget
    ) { categories, selectedCategory, memos, isEditorVisible, editTarget ->
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
                    editTarget = editTarget,
                    mode = editTarget.asEditorMode(),
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
            if (deletedCategoryId == editTarget.value?.categoryId) {
                resetEditor()
            }
        }
    }

    // Memo - C
    fun addMemo(categoryId: Long) {
        val memo = editTarget.value ?: return
        viewModelScope.launch {
            repository.insertMemo(memo.copy(categoryId = categoryId, contents = editorTextState.text.toMemoContents()))
            resetEditor()
        }
    }

    // Memo - U
    fun updateMemo() {
        val memo = editTarget.value ?: return
        viewModelScope.launch {
            repository.updateMemo(memo.copy(contents = editorTextState.text.toMemoContents()))
            resetEditor()
        }
    }

    // Memo - D
    fun deleteMemo(memoId: Long) {
        viewModelScope.launch {
            val deletedMemoId = repository.deleteMemo(memoId)
            // 삭제한 메모가 만약 MemoEditor에서 편집 중인 메모였다면, 해당 편집을 취소
            if (deletedMemoId == editTarget.value?.id) {
                resetEditor()
            }
        }
    }

    // event - etc
    fun selectCategory(categoryId: Long) {
        selectedCategoryId.value = categoryId
    }

    fun copyMemoToEditor(memo: Memo) {
        loadIntoEditor(memo)
        isEditorVisible.value = true
    }

    fun toggleEditorVisibility() {
        isEditorVisible.value = !isEditorVisible.value
    }

    fun clearEditor() {
        resetEditor()
    }

    private fun resetEditor() {
        loadIntoEditor(defaultEditTarget())
    }

    private fun loadIntoEditor(memo: Memo) {
        editTarget.value = memo
        editorTextState.setBulletedListText(memo.contents.toBulletedListText())
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

private fun defaultEditTarget(): Memo =
    Memo(
        categoryId = 0L,
        id = 0L,
        contents = emptyList()
    )

// Memo.id가 1 이상이면 Database에 있는 Memo라는 의미이므로 Update 모드
private fun Memo?.asEditorMode(): EditorMode =
    if (this != null && id > 0L) EditorMode.Update else EditorMode.Insert