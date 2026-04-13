package com.example.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.model.Memo
import com.example.model.MemoContent

class MemoPreviewParameterProvider : PreviewParameterProvider<List<Memo>> {
    override val values: Sequence<List<Memo>>
        get() = sequenceOf(
            List(5) { randomDummyMemo() }
        )

    private fun randomDummyMemo(): Memo =
        Memo(
            categoryId = 1L,
            id = memoId++,
            contents = List((1..4).random()) { randomDummyMemoContent() }
        )

    private fun randomDummyMemoContent(): MemoContent =
        MemoContent(
            id = memoContentId++,
            label = "label ${(1..99).random()}",
            text = "Random Generated Fake Memo"
        )

    private var memoId = 1L
    private var memoContentId = 1L
}