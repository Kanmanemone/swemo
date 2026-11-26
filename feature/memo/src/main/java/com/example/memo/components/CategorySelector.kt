package com.example.memo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Category
import com.example.ui.DevicePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategorySelector(
    categories: List<Category>,
    onAddCategoryButtonClick: () -> Unit,
    onCategorySelected: (Long) -> Unit,
    onRenameCategoryClick: (Category) -> Unit = {},
    onDeleteCategoryClick: (Category) -> Unit = {},
    onCategoryPositionsChange: (List<Long>) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val dragState = remember { ReorderableState() }
    // 저장된 순서가 Flow로 돌아올 때까지 드래그한 순서를 유지한다
    var categoriesInOrder by remember(categories) { mutableStateOf(categories) }

    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp),
        topBar = {
            TopAppBar(
                title = { Text("Category") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCategoryButtonClick,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary, // MemoChatBar 전송 버튼과 같은 색
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
            ) {
                Icon(
                    imageVector = SwemoIcons.Add,
                    contentDescription = null
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = paddingValues, // 목록이 topBar·내비게이션 바 밑까지 깔리고, 맨 처음·끝 항목만 비켜 선다
        ) {
            items(
                items = categoriesInOrder,
                key = { category -> category.id }
            ) { category ->
                var isMenuExpanded by remember(category.id) { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggedItem(this, dragState, category.id, listState)
                        // 끌리는 항목이 지나갈 때 아래 항목 글씨가 비쳐 겹치지 않게 불투명 배경을 깐다
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .reorderableClickable(
                                key = category.id,
                                dragState = dragState,
                                listState = listState,
                                onMove = { from, to -> categoriesInOrder = categoriesInOrder.moved(from, to) },
                                onDragEnd = { onCategoryPositionsChange(categoriesInOrder.map(Category::id)) },
                                onLongPress = { isMenuExpanded = true },
                                onDragAway = { isMenuExpanded = false },
                                onClick = { onCategorySelected(category.id) },
                            )
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = category.name)
                    }

                    Box(modifier = Modifier.matchParentSize()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(0.dp),
                        ) {
                            DropdownMenu(
                                expanded = isMenuExpanded,
                                onDismissRequest = { isMenuExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("카테고리 이름 변경") },
                                    onClick = {
                                        isMenuExpanded = false
                                        onRenameCategoryClick(category)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("카테고리 제거") },
                                    onClick = {
                                        isMenuExpanded = false
                                        onDeleteCategoryClick(category)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun CategorySelectorPreview() {
    SwemoTheme {
        CategorySelector(
            categories = listOf(
                Category(id = 1L, name = "전체"),
                Category(id = 2L, name = "업무"),
                Category(id = 3L, name = "개인"),
            ),
            onAddCategoryButtonClick = {},
            onCategorySelected = {},
        )
    }
}
