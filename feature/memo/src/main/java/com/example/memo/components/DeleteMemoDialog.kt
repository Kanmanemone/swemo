package com.example.memo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.ui.DevicePreviews

@Composable
internal fun DeleteMemoDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation,
            ) {
                Text(text = "삭제")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = "취소")
            }
        },
        icon = {
            Icon(
                imageVector = SwemoIcons.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(text = "메모 삭제")
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "이 메모를 삭제할까요?",
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}

@DevicePreviews
@Composable
fun DeleteMemoDialogPreview() {
    SwemoTheme {
        DeleteMemoDialog(
            onDismissRequest = {},
            onConfirmation = {},
        )
    }
}