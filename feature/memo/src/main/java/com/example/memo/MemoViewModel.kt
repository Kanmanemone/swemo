package com.example.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MemoRepository
import com.example.model.Category
import com.example.model.Memo
import com.example.model.MemoContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val repository: MemoRepository
) : ViewModel() {

    // combine할 flow들
    private val categories: Flow<List<Category>> = repository.getCategory()

    private val selectedCategory = MutableStateFlow<Category?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val memos: Flow<List<Memo>> = selectedCategory
        .flatMapLatest { category ->
            if (category == null) flowOf(emptyList())
            else repository.getMemosByCategory(category.id)
        }

    private val isEditorVisible = MutableStateFlow(false)

    private val isAddCategoryDialogVisible = MutableStateFlow(false)

    private val editingMemo = MutableStateFlow<Memo?>(null)

    // flow들을 combine하여 최종 ui state 선언
    val uiState: StateFlow<MemoUiState> = combine(
        categories,
        selectedCategory,
        memos,
        isEditorVisible,
        isAddCategoryDialogVisible,
        editingMemo
    ) { values: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val categories = values[0] as List<Category>
        val selectedCategory = values[1] as Category?
        @Suppress("UNCHECKED_CAST")
        val memos = values[2] as List<Memo>
        val isEditorVisible = values[3] as Boolean
        val isAddCategoryDialogVisible = values[4] as Boolean
        val editingMemo = values[5] as Memo?

        MemoUiState(
            categories = categories,
            selectedCategory = selectedCategory,
            memos = memos,
            allLabels = memos.asSequence()
                .flatMap { it.contents }
                .map { it.label }
                .toSet(),
            isAddCategoryDialogVisible = isAddCategoryDialogVisible,
            editorState = MemoUiState.EditorState(
                isVisible = isEditorVisible,
                editingMemo = editingMemo
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingPolicy,
        initialValue = MemoUiState()
    )

    // events
    fun selectCategory(category: Category) {
        selectedCategory.value = category
        if (isEditorVisible.value) {
            editingMemo.value = createEditingMemo(category)
        }
    }

    fun toggleEditorVisibility() {
        val visible = !isEditorVisible.value
        isEditorVisible.value = visible
        editingMemo.value = selectedCategory.value?.takeIf { visible }?.let(::createEditingMemo)
    }

    fun changeAddCategoryDialogVisibility(visible: Boolean) {
        isAddCategoryDialogVisible.value = visible
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
        viewModelScope.launch {
            repository.insertMemo(currentEditingMemo)
            editingMemo.value = selectedCategory.value?.let(::createEditingMemo)
        }
    }

    init {
        // 첫 번째 카테고리를 자동으로 선택
        viewModelScope.launch {
            categories
                .filter { it.isNotEmpty() }
                .first()
                .let { list ->
                    if (selectedCategory.value == null) {
                        selectedCategory.value = list.first()
                    }
                }
        }
    }
}

private val SharingPolicy = SharingStarted.WhileSubscribed(5_000)

private fun createEditingMemo(category: Category): Memo = Memo(
    categoryId = category.id,
    id = 0L,
    contents = listOf(
        MemoContent(id = 0L, label = "label ${0 + 1}", text = "")
    )
)

private fun nextTemporaryId(memo: Memo): Long {
    val minId = memo.contents.minOfOrNull(MemoContent::id) ?: 0L
    return if (minId <= 0L) minId - 1L else -1L
}