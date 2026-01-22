package com.example.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.model.Memo
import com.example.model.MemoContent

class MemoPreviewParameterProvider : PreviewParameterProvider<List<Memo>> {
    override val values: Sequence<List<Memo>>
        get() = sequenceOf(
            listOf(
                Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "label 1", text = "Fake memo 1"))),
                Memo(categoryId = "1", id = "0", contents = listOf(MemoContent(label = "label 2", text = "Fake memo 2"))),
                Memo(categoryId = "2", id = "0", contents = listOf(MemoContent(label = "label 3", text = "Fake memo 3"))),
                Memo(
                    categoryId = "2", id = "0", contents = listOf(
                        MemoContent(label = "label 4", text = "Fake memo 4-1"),
                        MemoContent(label = "label 5", text = "Fake memo 4-2")
                    )
                ),
                Memo(
                    categoryId = "3", id = "0", contents = listOf(
                        MemoContent(label = "label 6", text = "Fake memo 5-1"),
                        MemoContent(label = "label 7", text = "Fake memo 5-2"),
                        MemoContent(label = "label 8", text = "Fake memo 5-3")
                    )
                ),
                Memo(
                    categoryId = "3", id = "0", contents = listOf(
                        MemoContent(label = "label 9", text = "Fake memo 6-1"),
                        MemoContent(label = "label 1", text = "Fake memo 6-2"),
                    )
                ),
            )
        )
}
