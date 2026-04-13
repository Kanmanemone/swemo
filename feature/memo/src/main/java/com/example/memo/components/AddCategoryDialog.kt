package com.example.memo.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.ui.DevicePreviews

@Composable
internal fun AddCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    var newCategoryName by rememberSaveable { mutableStateOf("") }
    val isConfirmEnabled = newCategoryName.trim().isNotBlank()

    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(newCategoryName)
                },
                enabled = isConfirmEnabled
            ) {
                Text(text = "확인")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = "취소")
            }
        },
        icon = {
            Icon(
                imageVector = SwemoIcons.Add,
                contentDescription = null
            )
        },
        title = {
            Text(text = "카테고리 추가")
        },
        text = {
            TextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                placeholder = {
                    Text("카테고리 이름")
                }
            )
        },
    )
}

@DevicePreviews
@Composable
fun AddDialogPreview() {
    SwemoTheme {
        AddCategoryDialog(
            onDismissRequest = {},
            onConfirmation = {},
        )
    }
}