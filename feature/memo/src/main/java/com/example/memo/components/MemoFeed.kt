package com.example.memo.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.Memo

@Composable
fun MemoFeed(
    memos: List<Memo>,
    modifier: Modifier = Modifier,
    onMemoClick: (Memo) -> Unit = {},
    onMemoLongClick: (Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
    ) {
        items(
            items = memos.asReversed(),
            key = Memo::id
        ) { memo ->
            Spacer(modifier = Modifier.height(4.dp))
            MemoCard(
                memo = memo,
                onClick = { onMemoClick(memo) },
                onLongClick = { onMemoLongClick(memo.id) }
            )
        }
    }
}