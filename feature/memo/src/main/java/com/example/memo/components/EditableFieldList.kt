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
fun EditableFieldList(
    memo: Memo,
    modifier: Modifier = Modifier,
    onMemoChange: (Memo) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(
                items = memo.contents,
                key = { content -> content.label }
            ) { content ->
                Surface(
                    tonalElevation = 20.dp
                ) {
                    MemoContentTextField(
                        label = content.label,
                        text = content.text,
                        onTextChange = { changedText ->
                            onMemoChange(
                                memo.updateContentText(
                                    label = content.label,
                                    text = changedText,
                                )
                            )
                        }
                    )
                }
            }
        }
    )
}

private fun Memo.updateContentText(
    label: String,
    text: String,
): Memo {
    return copy(
        contents = contents.map { currentContent ->
            if (currentContent.label == label) {
                currentContent.copy(text = text)
            } else {
                currentContent
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
                categoryId = "0",
                id = "0",
                contents = listOf(
                    MemoContent(label = "label 1", text = "Fake memo 7"),
                    MemoContent(label = "label 2", text = "Fake memo 7"),
                    MemoContent(label = "label 3", text = "Fake memo 7"),
                )
            )
        )
    }
}