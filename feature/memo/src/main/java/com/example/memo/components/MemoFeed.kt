package com.example.memo.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    val reorderState = rememberDragReorderState(listState, Memo::id)
    var menuMemoId by remember { mutableStateOf<Long?>(null) }
    // reverseLayout과 함께 써서 화면 위→아래가 memos 순서가 된다. 같은 인스턴스를 유지해야 임시 순서가 원본과 맞물린다
    val reversedMemos = remember(memos) { memos.asReversed() }

    LazyColumn(
        modifier = modifier,
        state = listState,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = reorderState.shown(reversedMemos),
            key = Memo::id
        ) { memo ->
            Spacer(modifier = Modifier.height(4.dp))
            MemoCard(
                memo = memo,
                modifier = Modifier.draggedItem(reorderState, memo.id),
                gestureModifier = Modifier.dragToReorder(
                    state = reorderState,
                    key = memo.id,
                    items = reversedMemos,
                    onLongPress = { menuMemoId = memo.id },
                    onReorder = { reordered -> onMemoPositionsChange(reordered.asReversed().map(Memo::id)) },
                ),
                isMenuExpanded = menuMemoId == memo.id,
                onClick = { onMemoClick(memo) },
                onLongClick = { menuMemoId = memo.id },
                onMenuDismiss = { menuMemoId = null },
                onDeleteClick = { onMemoDeleteClick(memo.id) },
            )
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
