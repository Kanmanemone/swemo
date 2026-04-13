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
internal fun RenameCategoryDialog(
    currentCategoryName: String,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    var updatedCategoryName by rememberSaveable(currentCategoryName) {
        mutableStateOf(currentCategoryName)
    }
    val trimmedCategoryName = updatedCategoryName.trim()
    val isConfirmEnabled = trimmedCategoryName.isNotBlank() && trimmedCategoryName != currentCategoryName

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(updatedCategoryName)
                },
                enabled = isConfirmEnabled
            ) {
                Text(text = "변경")
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
                imageVector = SwemoIcons.Edit,
                contentDescription = null
            )
        },
        title = {
            Text(text = "카테고리 이름 변경")
        },
        text = {
            TextField(
                value = updatedCategoryName,
                onValueChange = { updatedCategoryName = it },
                placeholder = {
                    Text("새 카테고리 이름")
                }
            )
        },
    )
}

@DevicePreviews
@Composable
fun RenameDialogPreview() {
    SwemoTheme {
        RenameCategoryDialog(
            currentCategoryName = "Example 카테고리",
            onDismissRequest = {},
            onConfirmation = {},
        )
    }
}