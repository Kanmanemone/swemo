package com.example.memo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.memo.components.DeleteCategoryDialog
import com.example.memo.components.MemoEditor
import com.example.memo.components.MemoFeed
import com.example.memo.components.RenameCategoryDialog
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
    var dialogState by rememberSaveable { mutableStateOf<MemoScreenDialog?>(null) }

    MemoScreen(
        uiState = uiState,
        drawerState = drawerState,
        dialogState = dialogState,
        // events
        onDialogChange = { dialogState = it },
        onCategorySelected = viewModel::selectCategory,
        onMemoChange = viewModel::updateEditingMemo,
        onAddContentClick = viewModel::addMemoContent,
        onAddMemoClick = viewModel::addMemo,
        onMemoEditorToggleButtonClick = viewModel::toggleEditorVisibility,
        onAddCategoryClick = viewModel::addCategory,
        onRenameCategoryClick = viewModel::renameSelectedCategory,
        onDeleteCategoryClick = viewModel::deleteSelectedCategory,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MemoScreen(
    uiState: MemoUiState,
    drawerState: DrawerState,
    dialogState: MemoScreenDialog?,
    // events
    onDialogChange: (MemoScreenDialog?) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onMemoChange: (Memo) -> Unit,
    onAddContentClick: () -> Unit,
    onAddMemoClick: () -> Unit,
    onMemoEditorToggleButtonClick: () -> Unit,
    onAddCategoryClick: (String) -> Unit,
    onRenameCategoryClick: (String) -> Unit,
    onDeleteCategoryClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CategorySelector(
                categories = uiState.categories,
                onCategorySelected = { categoryId ->
                    onCategorySelected(categoryId)
                    scope.launch { drawerState.close() }
                },
                onAddCategoryButtonClick = {
                    onDialogChange(MemoScreenDialog.CATEGORY_ADD)
                }
            )
        }
    ) {
        if (uiState.selectedCategory == null) {
            Scaffold {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onDialogChange(MemoScreenDialog.CATEGORY_ADD)
                        }
                    ) {
                        Text("새 카테고리 만들기")
                    }
                }
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(uiState.selectedCategory.name)
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                }
                            ) {
                                Icon(
                                    imageVector = SwemoIcons.SideNavigation,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            MemoScreenCategoryMenu(
                                onRenameClick = {
                                    onDialogChange(MemoScreenDialog.CATEGORY_RENAME)
                                },
                                onDeleteClick = {
                                    onDialogChange(MemoScreenDialog.CATEGORY_DELETE)
                                }
                            )
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
                                onMemoChange = onMemoChange,
                                onAddContentClick = onAddContentClick,
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
    }

    MemoScreenDialogHost(
        dialog = dialogState,
        targetCategory = uiState.selectedCategory,
        onDismiss = { onDialogChange(null) },
        onAddCategory = { name ->
            onAddCategoryClick(name)
            onDialogChange(null)
        },
        onRenameCategory = { name ->
            onRenameCategoryClick(name)
            onDialogChange(null)
        },
        onDeleteCategory = {
            onDeleteCategoryClick()
            onDialogChange(null)
        }
    )
}

@Composable
private fun MemoScreenCategoryMenu(
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = {
                isExpanded = !isExpanded
            }
        ) {
            Icon(
                imageVector = SwemoIcons.Menu,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("카테고리 이름 변경")
                },
                onClick = {
                    isExpanded = false
                    onRenameClick()
                }
            )
            DropdownMenuItem(
                text = {
                    Text("카테고리 제거")
                },
                onClick = {
                    isExpanded = false
                    onDeleteClick()
                }
            )
        }
    }
}

@Composable
private fun MemoScreenDialogHost(
    dialog: MemoScreenDialog?,
    targetCategory: Category?,
    onDismiss: () -> Unit,
    onAddCategory: (String) -> Unit,
    onRenameCategory: (String) -> Unit,
    onDeleteCategory: () -> Unit,
) {
    when (dialog) {
        MemoScreenDialog.CATEGORY_ADD ->
            AddCategoryDialog(
                onDismissRequest = onDismiss,
                onConfirmation = onAddCategory,
            )

        MemoScreenDialog.CATEGORY_RENAME ->
            if (targetCategory != null) {
                RenameCategoryDialog(
                    currentCategoryName = targetCategory.name,
                    onDismissRequest = onDismiss,
                    onConfirmation = onRenameCategory,
                )
            }

        MemoScreenDialog.CATEGORY_DELETE ->
            if (targetCategory != null) {
                DeleteCategoryDialog(
                    categoryName = targetCategory.name,
                    onDismissRequest = onDismiss,
                    onConfirmation = onDeleteCategory,
                )
            }

        null -> Unit
    }
}

internal enum class MemoScreenDialog {
    CATEGORY_ADD,
    CATEGORY_RENAME,
    CATEGORY_DELETE
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
                selectedCategory = Category(id = 1L, name = "category 1"),
                memos = memos,
                allLabels = setOf("label 1", "label 2")
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            dialogState = null,
            onDialogChange = {},
            onCategorySelected = {},
            onMemoChange = {},
            onAddContentClick = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryClick = {},
            onRenameCategoryClick = {},
            onDeleteCategoryClick = {},
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
                selectedCategory = Category(id = 1L, name = "category 1"),
                memos = memos,
                allLabels = setOf("label 3", "label 5"),
                editorState = MemoUiState.EditorState(
                    isVisible = true,
                    editingMemo = Memo(
                        categoryId = 0L,
                        id = 0L,
                        contents = listOf(
                            MemoContent(id = 0L, label = "label 3", text = "Fake memo 7"),
                            MemoContent(id = -1L, label = "label 5", text = "Fake memo 7"),
                            MemoContent(id = -2L, label = "label 7", text = "Fake memo 7"),
                        )
                    ),
                )
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            dialogState = null,
            onDialogChange = {},
            onCategorySelected = {},
            onMemoChange = {},
            onAddContentClick = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryClick = {},
            onRenameCategoryClick = {},
            onDeleteCategoryClick = {},
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
                selectedCategory = Category(id = 1L, name = "category 1"),
                memos = emptyList(),
                allLabels = emptySet(),
            ),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            dialogState = MemoScreenDialog.CATEGORY_ADD,
            onDialogChange = {},
            onCategorySelected = {},
            onMemoChange = {},
            onAddContentClick = {},
            onAddMemoClick = {},
            onMemoEditorToggleButtonClick = {},
            onAddCategoryClick = {},
            onRenameCategoryClick = {},
            onDeleteCategoryClick = {},
        )
    }
}

@Preview
@Composable
fun PreviewTestPreview() {
    Text("Hello")
}