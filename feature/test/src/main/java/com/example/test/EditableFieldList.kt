package com.example.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent
import com.example.ui.DevicePreviews

@Composable
fun EditableFieldList(
    memo: Memo,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            memo.contents.forEach { (label, text) ->
                item {
                    TextField(
                        value = text,
                        onValueChange = {

                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(label)
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