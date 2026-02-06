package com.example.test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.data.FakeMemoRepository
import com.example.data.MemoRepository
import com.example.designsystem.SwemoTheme
import com.example.model.Category
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews
import com.example.ui.MemoPreviewParameterProvider
import kotlinx.coroutines.launch

// state 주입용
@Composable
fun TestScreen(viewModel: ViewModel? = null) {
    val repository: MemoRepository = remember { FakeMemoRepository() }

    // category
    val categories by repository.getCategory().collectAsState(initial = emptyList())
    var selectedCategory: Category by remember { mutableStateOf(Category(id = "1", name = "category 1")) }

    // memo
    val memosFlow = remember(selectedCategory.id) {
        repository.getMemosByCategory(selectedCategory.id)
    }
    val memos by memosFlow.collectAsState(initial = emptyList())
    val allLabels by remember(memos) {
        derivedStateOf {
            memos
                .asSequence()
                .flatMap { it.contents }
                .map { it.label }
                .toSet()
        }
    }
    val editingMemo = Memo(
        categoryId = "0",
        id = "0",
        contents = listOf(
            MemoContent(label = "label 2", text = "Fake memo 7"),
            MemoContent(label = "label 3", text = "Fake memo 8"),
        )
    )

    // etc
    var isMemoEditorVisible by remember { mutableStateOf(false) }

    TestScreen(
        categories = categories,
        selectedCategory = selectedCategory,
        memos = memos,
        editingMemo = editingMemo,
        allLabels = allLabels,
        isMemoEditorVisible = isMemoEditorVisible,
        // events
        onCategorySelected = { category: Category ->
            selectedCategory = category
        },
        onAddMemoClick = {
            repository.insertMemo(
                Memo(
                    categoryId = selectedCategory.id,
                    id = "0",
                    contents = listOf(MemoContent(label = "label 4", text = "Inserted memo"))
                )
            )
        },
        onMemoEditorToggleButtonClick = {
            isMemoEditorVisible = !isMemoEditorVisible
        },
    )
}

// 순수 UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    categories: List<Category>,
    selectedCategory: Category,
    memos: List<Memo>,
    editingMemo: Memo?,
    allLabels: Set<String>,
    isMemoEditorVisible: Boolean,
    // events
    onCategorySelected: (Category) -> Unit,
    onAddMemoClick: () -> Unit,
    onMemoEditorToggleButtonClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CategorySelector(
                categories = categories,
                onCategorySelected = { category ->
                    onCategorySelected(category)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(selectedCategory.name)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.menu_24dp_5f6368_fill0_wght400_grad0_opsz24),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onAddMemoClick()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add_24dp_5f6368_fill0_wght400_grad0_opsz24),
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (isMemoEditorVisible) {
                    Surface(
                        tonalElevation = 4.dp
                    ) {
                        MemoEditor(
                            editingMemo = editingMemo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            allLabels = allLabels
                        )
                    }
                }
            },
            floatingActionButton = {
                Button(
                    onClick = {
                        onMemoEditorToggleButtonClick()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_notes_24dp_5f6368_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { paddingValues ->
            MemoFeed(
                memos = memos,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@DevicePreviews
@Composable
fun TestScreenPreview_Default(
    @PreviewParameter(MemoPreviewParameterProvider::class)
    memos: List<Memo>,
) {
    SwemoTheme {
        TestScreen(
            categories = emptyList(),
            selectedCategory = Category(id = "1", name = "category 1"),
            memos = memos,
            allLabels = setOf("label 1", "label 2", "label 3", "label 4", "label 5", "label 6", "label 7", "label 8", "label 9"),
            isMemoEditorVisible = false,
            editingMemo = Memo(
                categoryId = "0",
                id = "0",
                contents = listOf(
                    MemoContent(label = "label 3", text = "Fake memo 7"),
                    MemoContent(label = "label 5", text = "Fake memo 7"),
                    MemoContent(label = "label 7", text = "Fake memo 7"),
                )
            ),
            // events
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun TestScreenPreview_MemoEditorVisible(
    @PreviewParameter(MemoPreviewParameterProvider::class)
    memos: List<Memo>,
) {
    SwemoTheme {
        TestScreen(
            categories = emptyList(),
            selectedCategory = Category(id = "1", name = "category 1"),
            memos = memos,
            allLabels = setOf("label 1", "label 2", "label 3", "label 4", "label 5", "label 6", "label 7", "label 8", "label 9"),
            isMemoEditorVisible = true,
            editingMemo = Memo(
                categoryId = "0",
                id = "0",
                contents = listOf(
                    MemoContent(label = "label 3", text = "Fake memo 7"),
                    MemoContent(label = "label 5", text = "Fake memo 7"),
                    MemoContent(label = "label 7", text = "Fake memo 7"),
                )
            ),
            // events
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
        )
    }
}

@Preview
@Composable
fun PreviewTestPreview() {
    Text("Hello")
}