package com.example.test

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.SwemoTheme
import com.example.model.Memo
import com.example.model.MemoContent

@Composable
fun MemoEditor(
    editingMemo: Memo?,
    modifier: Modifier = Modifier,
    allLabels: Set<String>
) {
    if (editingMemo != null) {
        val usedLabels: Set<String> = editingMemo.contents
            .map { it.label }
            .toSet()
        val availableLabels: Set<String> = allLabels - usedLabels

        Column(
            modifier = modifier
        ) {
            EditableFieldList(
                memo = editingMemo,
                modifier = Modifier.fillMaxWidth()
            )
            LabelChipGroup(
                labels = availableLabels,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun MemoEditorPreview() {
    SwemoTheme {
        MemoEditor(
            Memo(
                categoryId = "0",
                id = "0",
                contents = listOf(
                    MemoContent(label = "label 1", text = "Fake memo 7"),
                    MemoContent(label = "label 2", text = "Fake memo 7"),
                    MemoContent(label = "label 3", text = "Fake memo 7"),
                )
            ),
            allLabels = setOf("label 1", "label 2", "label 3", "label 4", "label 5", "label 6", "label 7", "label 8", "label 9"),
        )
    }
}
