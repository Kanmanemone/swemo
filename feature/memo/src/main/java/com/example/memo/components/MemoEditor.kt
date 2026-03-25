package com.example.memo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@Composable
fun MemoEditor(
    editingMemo: Memo?,
    modifier: Modifier = Modifier,
    onAddMemoClick: () -> Unit = {},
) {
    if (editingMemo != null) {
        Column(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
        ) {
            EditableFieldList(
                memo = editingMemo,
                modifier = Modifier.fillMaxWidth()
            )
            MemoEditorActionBar(
                onAddMemoClick = onAddMemoClick
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoEditorPreview() {
    SwemoTheme {
        MemoEditor(
            Memo(
                categoryId = "0",
                id = "0",
                contents = listOf(
                    MemoContent(label = "label 1", text = "Fake memo 7"),
                    MemoContent(label = "label 2", text = "Fake memo 7"),
                    MemoContent(label = "label 3", text = "Fake memo 7"),
                )
            ),
        )
    }
}
