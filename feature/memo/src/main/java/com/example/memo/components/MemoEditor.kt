package com.example.memo.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.designsystem.theme.SwemoTheme
import com.example.memo.EditorMode
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@Composable
internal fun MemoEditor(
    modifier: Modifier = Modifier,
    editTarget: Memo?,
    textState: TextFieldState,
    mode: EditorMode,
    onAddMemoClick: () -> Unit = {},
    onEditMemoClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {},
) {
    if (editTarget != null) {
        MemoChatBar(
            state = textState,
            mode = mode,
            modifier = modifier
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
            onSendClick = when (mode) {
                EditorMode.Insert -> onAddMemoClick
                EditorMode.Update -> onEditMemoClick
            },
            onClearAllClick = onClearAllClick,
        )
    }
}

@DevicePreviews
@Composable
fun MemoEditorPreview() {
    SwemoTheme {
        MemoEditor(
            editTarget = Memo(
                categoryId = 0L,
                id = 0L,
                contents = listOf(
                    MemoContent(id = 1L, label = "label 1", text = "Fake memo 7"),
                    MemoContent(id = 2L, label = "label 2", text = "Fake memo 7"),
                    MemoContent(id = 3L, label = "label 3", text = "Fake memo 7"),
                )
            ),
            textState = remember { bulletedListTextFieldState("label 1: Fake memo 7\n사과: 빨간 것\n라벨 없는 줄") },
            mode = EditorMode.Insert,
        )
    }
}
