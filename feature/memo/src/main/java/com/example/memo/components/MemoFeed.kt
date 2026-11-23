package com.example.memo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.ui.DevicePreviews
import com.example.ui.MemoPreviewParameterProvider

@Composable
internal fun MemoFeed(
    memos: List<Memo>,
    modifier: Modifier = Modifier,
    onMemoClick: (Memo) -> Unit = {},
    onMemoDeleteClick: (Long) -> Unit = {},
    onMemoPositionsChange: (List<Long>) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val dragState = remember { ReorderableState() }
    // 드래그 중에는 화면 순서를 여기서 바꾸고, 손을 뗄 때만 저장한다
    var itemsInListOrder by remember(memos) { mutableStateOf(memos.asReversed()) }

    LazyColumn(
        modifier = modifier,
        state = listState,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = itemsInListOrder,
            key = Memo::id
        ) { memo ->
            var isMenuExpanded by remember { mutableStateOf(false) }

            Box(
                // 간격을 항목에 포함해야 자리 이동 애니메이션이 카드와 함께 움직인다 (reverseLayout이라 아래쪽)
                modifier = Modifier
                    .draggedItem(this, dragState, memo.id, listState)
                    .padding(bottom = 4.dp),
            ) {
                MemoCard(
                    memo = memo,
                    isMenuExpanded = isMenuExpanded,
                    onMenuExpandedChange = { isMenuExpanded = it },
                    onDeleteClick = { onMemoDeleteClick(memo.id) },
                )

                // 카드의 터치 입력은 모두 이 투명한 덮개가 받는다
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(role = Role.Button) { onMemoClick(memo) }
                        .longPressReorderable(
                            key = memo.id,
                            dragState = dragState,
                            listState = listState,
                            onMove = { from, to -> itemsInListOrder = itemsInListOrder.moved(from, to) },
                            onDragEnd = { onMemoPositionsChange(itemsInListOrder.asReversed().map(Memo::id)) },
                            onLongPress = { isMenuExpanded = true },
                            onDragAway = { isMenuExpanded = false },
                        ),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun MemoFeedPreview(
    @PreviewParameter(MemoPreviewParameterProvider::class)
    memos: List<Memo>,
) {
    SwemoTheme {
        MemoFeed(
            memos = memos
        )
    }
}
