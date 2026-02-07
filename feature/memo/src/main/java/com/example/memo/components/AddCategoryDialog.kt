package com.example.memo.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.DevicePreviews
import com.example.designsystem.icon.SwemoIcons

@Composable
fun AddCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    icon: ImageVector,
    dialogTitle: String,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(text = "추가")
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
                imageVector = icon,
                contentDescription = null
            )
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            content()
        },
    )
}

@DevicePreviews
@Composable
fun AddCategoryDialogPreview() {
    AddCategoryDialog(
        onDismissRequest = {},
        onConfirmation = {},
        icon = SwemoIcons.Add,
        dialogTitle = "Add Category",
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text("Category name")
            }
        )
    }
}