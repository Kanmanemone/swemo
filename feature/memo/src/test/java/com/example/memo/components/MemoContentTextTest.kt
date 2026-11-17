package com.example.memo.components

import com.example.model.MemoContent
import org.junit.Assert.assertEquals
import org.junit.Test

class MemoContentTextTest {
    @Test
    fun parsesLabelledAndPlainLines() {
        val contents = "사과: 빨간 것\n라벨 없는 줄\n사과:붙여씀\n\n: 빈 라벨".toMemoContents()
        assertEquals(
            listOf(
                MemoContent(id = -1L, label = "사과", text = "빨간 것"),
                MemoContent(id = -2L, label = "", text = "라벨 없는 줄"),
                MemoContent(id = -3L, label = "", text = "사과:붙여씀"), // `:` 뒤 공백이 없으면 라벨이 아니다
                MemoContent(id = -4L, label = "", text = ""),
                MemoContent(id = -5L, label = "", text = ": 빈 라벨"),
            ),
            contents,
        )
    }

    @Test
    fun roundTripsThroughText() {
        val text = "label 1: Fake memo\n본문만\n"
        assertEquals(text, text.toMemoContents().toBulletedListText())
    }

    @Test
    fun emptyTextIsOneEmptyContent() {
        assertEquals(listOf(MemoContent(id = -1L, label = "", text = "")), "".toMemoContents())
    }
}
