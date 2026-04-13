package com.example.memo.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@Composable
internal fun EditableFieldList(
    memo: Memo,
    modifier: Modifier = Modifier,
    onMemoContentTextChange: (Long, String) -> Unit = { _, _ -> },
    onMemoContentRemove: (Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(
                items = memo.contents,
                key = MemoContent::id
            ) { content ->
                Surface(
                    tonalElevation = 20.dp
                ) {
                    MemoContentTextField(
                        label = content.label,
                        text = content.text,
                        onTextChange = { changedText ->
                            onMemoContentTextChange(content.id, changedText)
                        },
                        onRemoveClick = {
                            onMemoContentRemove(content.id)
                        }
                    )
                }
            }
        }
    )
}

@DevicePreviews
@Composable
fun EditableFieldListPreview() {
    SwemoTheme {
        EditableFieldList(
            Memo(
                categoryId = 0L,
                id = 0L,
                contents = listOf(
                    MemoContent(id = 1L, label = "label 1", text = "Fake memo 7"),
                    MemoContent(id = 2L, label = "label 2", text = "Fake memo 7"),
                    MemoContent(id = 3L, label = "label 3", text = "Fake memo 7"),
                )
            )
        )
    }
}