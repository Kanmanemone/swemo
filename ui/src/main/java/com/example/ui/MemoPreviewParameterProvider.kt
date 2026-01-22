package com.example.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.model.Memo
import com.example.model.MemoContent

class MemoPreviewParameterProvider : PreviewParameterProvider<List<Memo>> {
    override val values: Sequence<List<Memo>>
        get() = sequenceOf(
            listOf(
                Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 1 (category 1)"))),
                Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 2 (category 1)"))),
                Memo(categoryId = "2", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 3 (category 2)"))),
                Memo(categoryId = "2", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 4 (category 2)"))),
                Memo(categoryId = "3", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 5 (category 3)"))),
                Memo(
                    categoryId = "3",
                    id = "0",
                    contents = listOf(
                        MemoContent(label = "label 1", text = "Fake memo 6-1 (category 3)"),
                        MemoContent(label = "label 2", text = "Fake memo 6-2 (category 3)"),
                        MemoContent(label = "label 3", text = "Fake memo 6-3 (category 3)"),
                    )
                ),
            )
        )
}
