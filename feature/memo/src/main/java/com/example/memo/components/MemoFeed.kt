package com.example.memo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.ui.DevicePreviews
import com.example.ui.MemoPreviewParameterProvider

@Composable
internal fun MemoFeed(
    memos: List<Memo>,
    editingMemoId: Long? = null,
    modifier: Modifier = Modifier,
    onMemoClick: (Memo) -> Unit = {},
    onMemoMoveCategoryClick: (Memo) -> Unit = {},
    onMemoDeleteClick: (Long) -> Unit = {},
    onMemoPositionsChange: (List<Long>) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    val focusManager = LocalFocusManager.current
    val dragState = remember { ReorderableState() }
    // 드래그 중에는 화면 순서를 여기서 바꾸고, 손을 뗄 때만 저장한다
    var itemsInListOrder by remember(memos) {
        // 맨 아래에 있었다면 key 기준 위치 유지 대신 맨 아래를 보여줘 새 메모가 보이게 한다. 스크롤해 둔 상태면 그대로 둔다
        if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) listState.requestScrollToItem(0)
        mutableStateOf(memos.asReversed())
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        reverseLayout = true,
        contentPadding = contentPadding,
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
                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
            ) {
                MemoCard(
                    memo = memo,
                    isEditing = memo.id == editingMemoId,
                    isMenuExpanded = isMenuExpanded,
                    onMenuExpandedChange = { isMenuExpanded = it },
                    onMoveCategoryClick = { onMemoMoveCategoryClick(memo) },
                    onDeleteClick = { onMemoDeleteClick(memo.id) },
                )

                // 카드의 터치 입력은 모두 이 투명한 덮개가 받는다
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(MemoCardShape)
                        .reorderableClickable(
                            key = memo.id,
                            dragState = dragState,
                            listState = listState,
                            onMove = { from, to -> itemsInListOrder = itemsInListOrder.moved(from, to) },
                            onDragEnd = { onMemoPositionsChange(itemsInListOrder.asReversed().map(Memo::id)) },
                            onLongPress = {
                                focusManager.clearFocus() // 입력란에 포커스가 남아 있으면 메뉴가 닫힐 때 키보드가 다시 뜬다
                                isMenuExpanded = true
                            },
                            onDragAway = { isMenuExpanded = false },
                            onClick = { onMemoClick(memo) },
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
