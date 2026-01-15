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
import androidx.compose.ui.unit.dp
import com.example.data.FakeMemoRepository
import com.example.data.MemoRepository
import com.example.model.Category
import com.example.model.Memo
import com.example.model.MemoContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen() {
    val scope = rememberCoroutineScope()

    val repository: MemoRepository = remember { FakeMemoRepository() }
    val categoriesFromRepo by repository.getCategory().collectAsState(initial = emptyList())
    val allMemosCategory = Category(id = "-1", name = "전체")
    val categories = listOf(allMemosCategory) + categoriesFromRepo
    var selectedCategory: Category by remember { mutableStateOf(allMemosCategory) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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
                            selectedCategory = category
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
                            repository.insertMemo(
                                Memo(
                                    categoryId = selectedCategory.id,
                                    id = "0",
                                    contents = listOf(MemoContent(label = "content", text = "Inserted memo (${selectedCategory.name})"))
                                )
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add_24dp_5f6368_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                }
            )

            val memosFlow = remember(selectedCategory.id) {
                when (selectedCategory.id) {
                    allMemosCategory.id -> repository.getMemos()
                    else -> repository.getMemosByCategory(selectedCategory.id)
                }
            }
            val memos by memosFlow.collectAsState(initial = emptyList())
            val memosText = memos.joinToString("\n") { it.contents.firstOrNull()?.text ?: "null" }
            Text(
                text = memosText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}