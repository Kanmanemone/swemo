package com.example.memo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.memo.EditorMode
import com.example.ui.DevicePreviews

@Composable
internal fun MemoEditorActionBar(
    mode: EditorMode = EditorMode.Insert,
    isClearAllEnabled: Boolean = false,
    isSubmitEnabled: Boolean = false,
    onAddContentClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {},
    onAddMemoClick: () -> Unit = {},
    onEditMemoClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onAddContentClick,
            shape = RoundedCornerShape(50),
        ) {
            Icon(
                imageVector = SwemoIcons.Add,
                contentDescription = null
            )
        }

        Button(
            onClick = onClearAllClick,
            enabled = isClearAllEnabled,
            shape = RoundedCornerShape(50),
        ) {
            Icon(
                imageVector = SwemoIcons.ClearAll,
                contentDescription = null
            )
        }

        Button(
            onClick = when (mode) {
                EditorMode.Insert -> onAddMemoClick
                EditorMode.Update -> onEditMemoClick
            },
            enabled = isSubmitEnabled,
            shape = RoundedCornerShape(50),
        ) {
            Icon(
                imageVector = when (mode) {
                    EditorMode.Insert -> SwemoIcons.Send
                    EditorMode.Update -> SwemoIcons.Edit
                },
                contentDescription = null
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoEditorActionBarPreview() {
    SwemoTheme {
        MemoEditorActionBar()
    }
}