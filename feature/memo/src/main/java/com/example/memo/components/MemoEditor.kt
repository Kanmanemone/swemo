package com.example.memo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.memo.EditorMode
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@Composable
internal fun MemoEditor(
    modifier: Modifier = Modifier,
    editingMemo: Memo?,
    mode: EditorMode,
    isClearAllEnabled: Boolean = false,
    isSubmitEnabled: Boolean = false,
    onMemoContentTextChange: (Long, String) -> Unit = { _, _ -> },
    onMemoContentRemove: (Long) -> Unit = {},
    onAddContentClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {},
    onAddMemoClick: () -> Unit = {},
    onEditMemoClick: () -> Unit = {}
) {
    if (editingMemo != null) {
        Column(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
        ) {
            EditableFieldList(
                memo = editingMemo,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                onMemoContentTextChange = onMemoContentTextChange,
                onMemoContentRemove = onMemoContentRemove
            )
            MemoEditorActionBar(
                mode = mode,
                isClearAllEnabled = isClearAllEnabled,
                isSubmitEnabled = isSubmitEnabled,
                onAddContentClick = onAddContentClick,
                onClearAllClick = onClearAllClick,
                onAddMemoClick = onAddMemoClick,
                onEditMemoClick = onEditMemoClick
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoEditorPreview() {
    SwemoTheme {
        MemoEditor(
            editingMemo = Memo(
                categoryId = 0L,
                id = 0L,
                contents = listOf(
                    MemoContent(id = 1L, label = "label 1", text = "Fake memo 7"),
                    MemoContent(id = 2L, label = "label 2", text = "Fake memo 7"),
                    MemoContent(id = 3L, label = "label 3", text = "Fake memo 7"),
                )
            ),
            mode = EditorMode.Insert,
            isClearAllEnabled = true,
            isSubmitEnabled = true,
            onMemoContentTextChange = { _, _ -> },
            onMemoContentRemove = {},
        )
    }
}