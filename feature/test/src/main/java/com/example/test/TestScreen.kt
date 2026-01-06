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
import com.example.data.Memo
import com.example.data.MemoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen() {
    val scope = rememberCoroutineScope()

    val repository: MemoRepository = remember { FakeMemoRepository() }
    var selectedCategory: String? by remember { mutableStateOf(null) }

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

                val categories by repository.getCategory().collectAsState(initial = emptyList())
                val categoryNames = listOf<String?>(null) + categories.map { it.name }
                categoryNames.forEach { categoryName ->
                    TextButton(
                        onClick = {
                            selectedCategory = categoryName
                            scope.launch { drawerState.close() }
                        }
                    ) {
                        Text(text = categoryName ?: "전체")
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
                    Text(selectedCategory ?: "전체")
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
                                Memo(content = "Inserted memo (${selectedCategory})", categoryName = selectedCategory)
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

            val memos by repository.getMemosByCategory(selectedCategory).collectAsState(initial = emptyList())
            val memosText = memos.joinToString("\n") { it.content }
            Text(
                text = memosText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}