package com.example.memo.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.DevicePreviews
import com.example.designsystem.icon.SwemoIcons

@Composable
fun MemoContentTextField(
    label: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = "$label: $text",
            onValueChange = {

            },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {

            }
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
        text = "text"
    )
}