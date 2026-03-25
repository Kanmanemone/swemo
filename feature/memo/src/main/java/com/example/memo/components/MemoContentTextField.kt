package com.example.memo.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.DevicePreviews

@Composable
fun MemoContentTextField(
    label: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {

            },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(text = label)
            }
        )
    }
}

@DevicePreviews
@Composable
fun MemoContentTextFieldPreview() {
    MemoContentTextField(
        label = "label",
        text = "text"
    )
}