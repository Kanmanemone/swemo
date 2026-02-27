package com.example.memo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.memo.components.AddCategoryDialog
import com.example.memo.components.CategorySelector
import com.example.memo.components.MemoEditor
import com.example.memo.components.MemoFeed
import com.example.model.Category
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.CategoryPreviewParameterProvider
import com.example.ui.DevicePreviews
import com.example.ui.MemoPreviewParameterProvider
import kotlinx.coroutines.launch

@Composable
fun MemoScreen(
    viewModel: MemoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ui-only states
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var isAddCategoryDialogVisible by remember { mutableStateOf(false) }

    MemoScreen(
        uiState = uiState,
        // ui-only states
        drawerState = drawerState,
        isAddCategoryDialogVisible = isAddCategoryDialogVisible,
        // events
        onCategorySelected = viewModel::selectCategory,
        onAddMemoClick = viewModel::addMemo,
        onMemoEditorToggleButtonClick = viewModel::toggleEditor,
        onAddCategoryDialogVisibleChange = { visible ->
            isAddCategoryDialogVisible = visible
        },
    )
}

// 순수 UI (Stateless)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MemoScreen(
    uiState: MemoUiState,
    // ui-only states
    drawerState: DrawerState,
    isAddCategoryDialogVisible: Boolean,
    // events
    onCategorySelected: (Category) -> Unit,
    onAddMemoClick: () -> Unit,
    onMemoEditorToggleButtonClick: () -> Unit,
    onAddCategoryDialogVisibleChange: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()

    // selectedCategory에 값이 담길 때 ui 표시
    val currentCategory = uiState.selectedCategory ?: return

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CategorySelector(
                categories = uiState.categories,
                onCategorySelected = { category ->
                    onCategorySelected(category)
                    scope.launch { drawerState.close() }
                },
                onAddCategoryButtonClick = {
                    onAddCategoryDialogVisibleChange(true)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(currentCategory.name)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = SwemoIcons.Menu,
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
                                imageVector = SwemoIcons.Add,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (uiState.editorState.isVisible) {
                    Surface(
                        tonalElevation = 4.dp
                    ) {
                        MemoEditor(
                            editingMemo = uiState.editorState.editingMemo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            allLabels = uiState.allLabels
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
                        imageVector = SwemoIcons.AddNotes,
                        contentDescription = null
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { paddingValues ->
            MemoFeed(
                memos = uiState.memos,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }

    if (isAddCategoryDialogVisible) {
        AddCategoryDialog(
            onDismissRequest = {
                onAddCategoryDialogVisibleChange(false)
            },
            onConfirmation = {},
            icon = SwemoIcons.Add,
            dialogTitle = "Add Category",
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text("Category name")
                }
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoScreenPreview_Default(
    @PreviewParameter(MemoPreviewParameterProvider::class)
    memos: List<Memo>,
) {
    SwemoTheme {
        MemoScreen(
            uiState = MemoUiState(
                categories = emptyList(),
                selectedCategory = Category(id = "1", name = "category 1"),
                memos = memos,
                allLabels = setOf("label 1", "label 2")
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            isAddCategoryDialogVisible = false,
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryDialogVisibleChange = {},
        )
    }
}

@DevicePreviews
@Composable
fun MemoScreenPreview_MemoEditorVisible(
    @PreviewParameter(MemoPreviewParameterProvider::class)
    memos: List<Memo>,
) {
    SwemoTheme {
        MemoScreen(
            uiState = MemoUiState(
                categories = emptyList(),
                selectedCategory = Category(id = "1", name = "category 1"),
                memos = memos,
                allLabels = setOf("label 3", "label 5"),
                editorState = MemoUiState.EditorState(
                    isVisible = true,
                    editingMemo = Memo(
                        categoryId = "1",
                        id = "0",
                        contents = listOf(MemoContent(label = "Label", text = "Editing..."))
                    )
                )
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            isAddCategoryDialogVisible = false,
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryDialogVisibleChange = {},
        )
    }
}

@DevicePreviews
@Composable
fun MemoScreenPreview_AddCategoryDialogVisible(
    @PreviewParameter(CategoryPreviewParameterProvider::class)
    categories: List<Category>,
) {
    SwemoTheme {
        MemoScreen(
            uiState = MemoUiState(
                categories = categories,
                selectedCategory = Category(id = "1", name = "category 1"),
                memos = emptyList(),
                allLabels = emptySet()
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            isAddCategoryDialogVisible = true,
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryDialogVisibleChange = {},
        )
    }
}

@Preview
@Composable
fun PreviewTestPreview() {
    Text("Hello")
}
