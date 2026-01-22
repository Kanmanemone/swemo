package com.example.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
    val categoriesFromRepo by repository.getCategory().collectAsState(initial = emptyList())
    val allMemosCategory = Category(id = "-1", name = "전체 메모")
    val categories = listOf(allMemosCategory) + categoriesFromRepo
    var selectedCategory: Category by remember { mutableStateOf(allMemosCategory) }

    // memo
    val memosFlow = remember(selectedCategory.id) {
        when (selectedCategory.id) {
            allMemosCategory.id -> repository.getMemos()
            else -> repository.getMemosByCategory(selectedCategory.id)
        }
    }
    val memos by memosFlow.collectAsState(initial = emptyList())

    TestScreen(
        categories = categories,
        selectedCategory = selectedCategory,
        memos = memos,
        // events
        onCategorySelected = { category: Category ->
            selectedCategory = category
        },
        onAddMemoClick = {
            repository.insertMemo(
                Memo(
                    categoryId = selectedCategory.id,
                    id = "0",
                    contents = listOf(MemoContent(label = "content", text = "Inserted memo (${selectedCategory.name})"))
                )
            )
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
    // events
    onCategorySelected: (Category) -> Unit,
    onAddMemoClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(color = MaterialTheme.colorScheme.surface),
            ) {
                TopAppBar(
                    title = { Text("Category") }
                )

                categories.forEach { category ->
                    TextButton(
                        onClick = {
                            onCategorySelected(category)
                            scope.launch { drawerState.close() }
                        }
                    ) {
                        Text(text = category.name)
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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

            val memosText = memos.joinToString("\n") { it.contents.first().text }
            Text(
                text = memosText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@DevicePreviews
@Composable
fun TestScreenPreview(
    @PreviewParameter(MemoPreviewParameterProvider::class)
    memos: List<Memo>,
) {
    SwemoTheme {
        TestScreen(
            categories = emptyList(),
            selectedCategory = Category(id = "-1", name = "전체 메모"),
            memos = memos,
            // events
            onCategorySelected = {},
            onAddMemoClick = {},
        )
    }
}