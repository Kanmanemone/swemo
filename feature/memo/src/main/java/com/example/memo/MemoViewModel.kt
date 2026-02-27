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

    private val isEditorVisible = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val memos: Flow<List<Memo>> = selectedCategory
        .flatMapLatest { category ->
            if (category == null) flowOf(emptyList())
            else repository.getMemosByCategory(category.id)
        }

    // flow들을 combine하여 최종 ui state 선언
    val uiState: StateFlow<MemoUiState> = combine(
        categories,
        selectedCategory,
        memos,
        isEditorVisible
    ) { categories, selectedCategory, memos, isEditorVisible ->
        MemoUiState(
            categories = categories,
            selectedCategory = selectedCategory,
            memos = memos,
            allLabels = memos.asSequence()
                .flatMap { it.contents }
                .map { it.label }
                .toSet(),
            editorState = MemoUiState.EditorState(
                isVisible = isEditorVisible,
                editingMemo = selectedCategory?.let {
                    Memo(
                        categoryId = it.id,
                        id = "0",
                        contents = listOf(
                            MemoContent(label = "Label", text = "Editing..")
                        )
                    )
                }
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
    }

    fun toggleEditor() {
        isEditorVisible.value = !isEditorVisible.value
    }

    fun addMemo() {
        val currentCategory = selectedCategory.value ?: return
        viewModelScope.launch {
            repository.insertMemo(
                Memo(
                    categoryId = currentCategory.id,
                    id = "0",
                    contents = listOf(
                        MemoContent(label = "New1", text = "Added via ViewModel"),
                        MemoContent(label = "New2", text = "Added via ViewModel")
                    )
                )
            )
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
