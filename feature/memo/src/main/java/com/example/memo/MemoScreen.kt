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
import androidx.compose.runtime.rememberCoroutineScope
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

    // ui element state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    MemoScreen(
        uiState = uiState,
        drawerState = drawerState,
        // events
        onCategorySelected = viewModel::selectCategory,
        onAddMemoClick = viewModel::addMemo,
        onMemoEditorToggleButtonClick = viewModel::toggleEditorVisibility,
        onAddCategoryDialogVisibilityChange = viewModel::changeAddCategoryDialogVisibility,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MemoScreen(
    uiState: MemoUiState,
    drawerState: DrawerState,
    onCategorySelected: (Category) -> Unit,
    onAddMemoClick: () -> Unit,
    onMemoEditorToggleButtonClick: () -> Unit,
    onAddCategoryDialogVisibilityChange: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
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
                    onAddCategoryDialogVisibilityChange(true)
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
                            onAddMemoClick = onAddMemoClick
                        )
                    }
                }
            },
            floatingActionButton = {
                Button(
                    onClick = onMemoEditorToggleButtonClick
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

    if (uiState.isAddCategoryDialogVisible) {
        AddCategoryDialog(
            onDismissRequest = {
                onAddCategoryDialogVisibilityChange(false)
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
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryDialogVisibilityChange = {},
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
                        categoryId = "0",
                        id = "0",
                        contents = listOf(
                            MemoContent(label = "label 3", text = "Fake memo 7"),
                            MemoContent(label = "label 5", text = "Fake memo 7"),
                            MemoContent(label = "label 7", text = "Fake memo 7"),
                        )
                    ),
                )
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryDialogVisibilityChange = {},
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
                allLabels = emptySet(),
                isAddCategoryDialogVisible = true
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            onCategorySelected = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryDialogVisibilityChange = {},
        )
    }
}

@Preview
@Composable
fun PreviewTestPreview() {
    Text("Hello")
}
