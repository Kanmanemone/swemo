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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.foundation.text.input.TextFieldState
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
import com.example.memo.components.bulletedListTextFieldState
import com.example.model.MemoContent
import com.example.ui.CategoryPreviewParameterProvider
import com.example.ui.DevicePreviews
import com.example.ui.MemoPreviewParameterProvider
import kotlinx.coroutines.launch
import java.io.Serializable

@Composable
fun MemoScreen(
    viewModel: MemoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ui element state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var dialogState by rememberSaveable { mutableStateOf<MemoScreenDialog?>(null) }

    MemoScreen(
        // states
        uiState = uiState,
        drawerState = drawerState,
        dialogState = dialogState,
        editorTextState = viewModel.editorTextState,
        // events - crud
        /* Category - C */ onAddCategoryClick = viewModel::addCategory,
        /* Category - U */ onRenameCategoryClick = viewModel::renameCategory,
        /* Category - U */ onCategoryPositionsChange = viewModel::updateCategoryPositions,
        /* Category - D */ onDeleteCategoryClick = viewModel::deleteCategory,
        /* Memo - C */ onAddMemoClick = viewModel::addMemo,
        /* Memo - U */ onEditMemoClick = viewModel::updateMemo,
        /* Memo - U */ onMemoPositionsChange = viewModel::updateMemoPositions,
        /* Memo - D */ onDeleteMemoClick = viewModel::deleteMemo,
        // event - etc
        onDialogRequested = { dialogState = it },
        onCategorySelected = viewModel::selectCategory,
        onMemoClick = viewModel::copyMemoToEditor,
        onClearAllClick = viewModel::clearEditor,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoScreen(
    // states
    uiState: MemoUiState,
    drawerState: DrawerState,
    dialogState: MemoScreenDialog?,
    editorTextState: TextFieldState,
    // events - crud
    /* Category - C */ onAddCategoryClick: (String) -> Unit,
    /* Category - U */ onRenameCategoryClick: (Long, String) -> Unit,
    /* Category - U */ onCategoryPositionsChange: (List<Long>) -> Unit,
    /* Category - D */ onDeleteCategoryClick: (Long) -> Unit,
    /* Memo - C */ onAddMemoClick: (Long) -> Unit,
    /* Memo - U */ onEditMemoClick: () -> Unit,
    /* Memo - U */ onMemoPositionsChange: (List<Long>) -> Unit,
    /* Memo - D */ onDeleteMemoClick: (Long) -> Unit,
    // event - etc
    onDialogRequested: (MemoScreenDialog?) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onMemoClick: (Memo) -> Unit,
    onClearAllClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val selectedCategory = uiState.selectedCategory
    val editorState = uiState.editorState
    val editTarget = editorState.editTarget

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CategorySelector(
                categories = uiState.categories,
                onAddCategoryButtonClick = {
                    onDialogRequested(MemoScreenDialog.CategoryAdd)
                },
                onCategorySelected = { categoryId ->
                    onCategorySelected(categoryId)
                    scope.launch { drawerState.close() }
                },
                onRenameCategoryClick = { category ->
                    onDialogRequested(
                        MemoScreenDialog.CategoryRename(category.id, category.name)
                    )
                },
                onDeleteCategoryClick = { category ->
                    onDialogRequested(
                        MemoScreenDialog.CategoryDelete(category.id, category.name)
                    )
                },
                onCategoryPositionsChange = onCategoryPositionsChange,
            )
        }
    ) {
        if (selectedCategory == null) {
            Scaffold {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onDialogRequested(MemoScreenDialog.CategoryAdd)
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
                            Text(selectedCategory.name)
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
                                    onDialogRequested(
                                        MemoScreenDialog.CategoryRename(
                                            categoryId = selectedCategory.id,
                                            categoryName = selectedCategory.name
                                        )
                                    )
                                },
                                onDeleteClick = {
                                    onDialogRequested(
                                        MemoScreenDialog.CategoryDelete(
                                            categoryId = selectedCategory.id,
                                            categoryName = selectedCategory.name
                                        )
                                    )
                                },
                            )
                        }
                    )
                },
                bottomBar = {
                    Surface {
                        MemoEditor(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            editTarget = editTarget,
                            textState = editorTextState,
                            mode = editorState.mode,
                            onAddMemoClick = { onAddMemoClick(selectedCategory.id) },
                            onEditMemoClick = onEditMemoClick,
                            onClearAllClick = onClearAllClick,
                        )
                    }
                },
            ) { paddingValues ->
                MemoFeed(
                    memos = uiState.memos,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onMemoClick = onMemoClick,
                    onMemoDeleteClick = onDeleteMemoClick,
                    onMemoPositionsChange = onMemoPositionsChange,
                )
            }
        }

        MemoScreenDialogHost(
            dialogState = dialogState,
            onDialogRequested = onDialogRequested,
            onAddCategoryClick = onAddCategoryClick,
            onRenameCategoryClick = onRenameCategoryClick,
            onDeleteCategoryClick = onDeleteCategoryClick,
        )
    }
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
    dialogState: MemoScreenDialog?,
    onDialogRequested: (MemoScreenDialog?) -> Unit,
    onAddCategoryClick: (String) -> Unit,
    onRenameCategoryClick: (Long, String) -> Unit,
    onDeleteCategoryClick: (Long) -> Unit,
) {
    when (dialogState) {
        MemoScreenDialog.CategoryAdd ->
            AddCategoryDialog(
                onDismissRequest = { onDialogRequested(null) },
                onConfirmation = { name ->
                    onAddCategoryClick(name)
                    onDialogRequested(null)
                },
            )

        is MemoScreenDialog.CategoryRename ->
            RenameCategoryDialog(
                categoryName = dialogState.categoryName,
                onDismissRequest = { onDialogRequested(null) },
                onConfirmation = { updatedName ->
                    onRenameCategoryClick(dialogState.categoryId, updatedName)
                    onDialogRequested(null)
                },
            )

        is MemoScreenDialog.CategoryDelete ->
            DeleteCategoryDialog(
                categoryName = dialogState.categoryName,
                onDismissRequest = { onDialogRequested(null) },
                onConfirmation = {
                    onDeleteCategoryClick(dialogState.categoryId)
                    onDialogRequested(null)
                },
            )

        null -> Unit
    }
}

private sealed interface MemoScreenDialog : Serializable {
    data object CategoryAdd : MemoScreenDialog {
        private fun readResolve(): Any = CategoryAdd
    }

    data class CategoryRename(val categoryId: Long, val categoryName: String) : MemoScreenDialog

    data class CategoryDelete(val categoryId: Long, val categoryName: String) : MemoScreenDialog

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
            editorTextState = remember { bulletedListTextFieldState() },
            onAddCategoryClick = {},
            onRenameCategoryClick = { _, _ -> },
            onCategoryPositionsChange = {},
            onDeleteCategoryClick = {},
            onAddMemoClick = {},
            onEditMemoClick = {},
            onMemoPositionsChange = {},
            onDeleteMemoClick = {},
            onDialogRequested = {},
            onCategorySelected = {},
            onMemoClick = {},
            onClearAllClick = {},
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
                    editTarget = Memo(
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
            editorTextState = remember { bulletedListTextFieldState("label 3: Fake memo 7\nlabel 5: Fake memo 7\nlabel 7: Fake memo 7") },
            onAddCategoryClick = {},
            onRenameCategoryClick = { _, _ -> },
            onCategoryPositionsChange = {},
            onDeleteCategoryClick = {},
            onAddMemoClick = {},
            onEditMemoClick = {},
            onMemoPositionsChange = {},
            onDeleteMemoClick = {},
            onDialogRequested = {},
            onCategorySelected = {},
            onMemoClick = {},
            onClearAllClick = {},
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
            dialogState = MemoScreenDialog.CategoryAdd,
            editorTextState = remember { bulletedListTextFieldState() },
            onAddCategoryClick = {},
            onRenameCategoryClick = { _, _ -> },
            onCategoryPositionsChange = {},
            onDeleteCategoryClick = {},
            onAddMemoClick = {},
            onEditMemoClick = {},
            onMemoPositionsChange = {},
            onDeleteMemoClick = {},
            onDialogRequested = {},
            onCategorySelected = {},
            onMemoClick = {},
            onClearAllClick = {},
        )
    }
}

@Preview
@Composable
fun PreviewTestPreview() {
    Text("Hello")
}
