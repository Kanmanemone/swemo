package com.example.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.model.Memo
import com.example.model.MemoContent

class MemoPreviewParameterProvider : PreviewParameterProvider<List<Memo>> {
    private var memoId = 1L
    private var memoContentId = 1L

    override val values: Sequence<List<Memo>>
        get() = sequenceOf(randomDummyMemos())

    private fun randomDummyMemos(): List<Memo> =
        List(5) { randomDummyMemo() }

    private fun randomDummyMemo(): Memo {
        val count = (1..4).random()

        return Memo(
            categoryId = 1L,
            id = memoId++,
            contents = List(count) { randomDummyMemoContent() }
        )
    }

    private fun randomDummyMemoContent(): MemoContent {
        return MemoContent(
            id = memoContentId++,
            label = "label ${(1..99).random()}",
            text = "Random Generated Fake Memo"
        )
    }
}