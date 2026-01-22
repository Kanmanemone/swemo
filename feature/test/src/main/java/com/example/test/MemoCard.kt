package com.example.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.Memo

@Composable
fun MemoCard(
    memo: Memo,
    modifier: Modifier = Modifier,
) {
    val contentTexts = memo.contents.joinToString(separator = "\n") { memoContent -> "${memoContent.label}: ${memoContent.text}" }

    Card(
        modifier = modifier.fillMaxWidth()
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