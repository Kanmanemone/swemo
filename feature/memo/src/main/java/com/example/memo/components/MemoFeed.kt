package com.example.memo.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = memos.asReversed(),
            key = Memo::id
        ) { memo ->
            var isMenuExpanded by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.height(4.dp))
            Box {
                MemoCard(
                    memo = memo,
                    isMenuExpanded = isMenuExpanded,
                    onMenuExpandedChange = { isMenuExpanded = it },
                    onDeleteClick = { onMemoDeleteClick(memo.id) },
                )
                // MemoCard 위를 덮는 투명한 터치 영역. 카드의 터치 입력은 모두 여기서 받는다
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .combinedClickable(
                            onClick = { onMemoClick(memo) },
                            onLongClickLabel = "메모 메뉴 열기",
                            onLongClick = { isMenuExpanded = true },
                            role = Role.Button,
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
