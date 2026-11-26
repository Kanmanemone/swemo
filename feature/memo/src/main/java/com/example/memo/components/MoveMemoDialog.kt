package com.example.memo.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.designsystem.icon.SwemoIcons
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Category
import com.example.ui.DevicePreviews

/**
 * @param currentCategoryName 메모가 지금 속한 카테고리 이름. 제목 아래에 "이름 →"으로 보인다
 * @param categories 옮길 수 있는 카테고리. 메모가 지금 속한 카테고리는 호출부가 빼고 넘긴다
 */
@Composable
internal fun MoveMemoDialog(
    currentCategoryName: String,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    onConfirmation: (Long) -> Unit,
) {
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { selectedCategoryId?.let(onConfirmation) },
                enabled = selectedCategoryId != null,
            ) {
                Text(text = "이동")
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
                imageVector = SwemoIcons.DriveFileMove,
                contentDescription = null
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "카테고리 이동")
                Text(
                    text = "$currentCategoryName → ${categories.firstOrNull { it.id == selectedCategoryId }?.name.orEmpty()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        text = {
            if (categories.isEmpty()) {
                Text(text = "옮길 수 있는 카테고리가 없어요.")
            } else {
                LazyColumn(modifier = Modifier.selectableGroup()) {
                    items(
                        items = categories,
                        key = { category -> category.id }
                    ) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .selectable(
                                    selected = category.id == selectedCategoryId,
                                    onClick = { selectedCategoryId = category.id },
                                    role = Role.RadioButton,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = category.id == selectedCategoryId,
                                onClick = null, // Row가 클릭을 받는다
                            )
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(start = 12.dp),
                            )
                        }
                    }
                }
            }
        },
    )
}

@DevicePreviews
@Composable
fun MoveMemoDialogPreview() {
    SwemoTheme {
        MoveMemoDialog(
            currentCategoryName = "전체",
            categories = listOf(
                Category(id = 2L, name = "업무"),
                Category(id = 3L, name = "개인"),
            ),
            onDismissRequest = {},
            onConfirmation = {},
        )
    }
}
