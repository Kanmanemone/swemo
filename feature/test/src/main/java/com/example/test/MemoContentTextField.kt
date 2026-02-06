package com.example.test

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.ui.DevicePreviews

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
                painter = painterResource(R.drawable.remove_24dp_5f6368_fill0_wght400_grad0_opsz24),
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