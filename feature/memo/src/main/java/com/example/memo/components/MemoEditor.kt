package com.example.memo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
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
        Column(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
        ) {
            val textStyle = bulletedListTextStyle()
            BulletedListTextField(
                state = textState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .padding(vertical = 8.dp),
                textStyle = textStyle,
                cursorBrush = SolidColor(textStyle.color), // 기본값(검정)은 다크모드에서 안 보인다
                pillStyle = PillStyle(color = MaterialTheme.colorScheme.secondaryContainer),
            )
            MemoEditorActionBar(
                mode = mode,
                isClearAllEnabled = mode == EditorMode.Update || !textState.isBulletedListEmpty(),
                onAddMemoClick = onAddMemoClick,
                onEditMemoClick = onEditMemoClick,
                onClearAllClick = onClearAllClick,
            )
        }
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