package com.example.memo.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.ui.DevicePreviews

@Composable
fun MemoContentTextField(
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
    onRemoveClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = label)
            }
        )
        IconButton(
            onClick = onRemoveClick,
        ) {
            Icon(
                imageVector = SwemoIcons.Remove,
                contentDescription = null
            )
        }
    }
}

@DevicePreviews
@Composable
fun MemoContentTextFieldPreview() {
    MemoContentTextField(
        label = "label",
        text = "text",
        onTextChange = {}
    )
}