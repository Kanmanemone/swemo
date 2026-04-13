package com.example.memo.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MemoCard(
    memo: Memo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val contentTexts = memo.contents.joinToString(separator = "\n") { memoContent -> "${memoContent.label}: ${memoContent.text}" }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = contentTexts,
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoCardPreview() {
    SwemoTheme {
        MemoCard(
            memo = Memo(
                categoryId = 0L,
                id = 0L,
                contents = listOf(
                    MemoContent(id = 1L, label = "label 1", text = "Fake memo 7"),
                    MemoContent(id = 2L, label = "label 2", text = "Fake memo 7"),
                    MemoContent(id = 3L, label = "label 3", text = "Fake memo 7"),
                )
            )
        )
    }
}