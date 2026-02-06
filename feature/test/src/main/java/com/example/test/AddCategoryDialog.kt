package com.example.test

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.ui.DevicePreviews

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
        icon = ImageVector.vectorResource(R.drawable.add_24dp_5f6368_fill0_wght400_grad0_opsz24),
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