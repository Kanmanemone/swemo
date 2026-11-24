package com.example.memo.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.model.Memo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * 메모 목록이 바뀔 때 [MemoFeed]의 스크롤 위치 규칙을 검사한다.
 * - 맨 아래(스크롤 0)에 있었다면 새 메모가 보이도록 맨 아래를 유지한다.
 * - 스크롤해 둔 상태면 화면이 움직이지 않는다.
 *
 * 카드 모양·글자에 기대지 않도록 LazyColumn의 key(= 메모 id)와 스크롤 위치만 본다.
 * reverseLayout이라 index 0(첫 번째로 보이는 항목)이 화면 맨 아래다.
 */
class MemoFeedScrollTest {
    @get:Rule
    val rule = createComposeRule()

    private val listState = LazyListState()

    // 화면보다 확실히 길게 만들어 어떤 기기에서도 스크롤이 생기게 한다
    private var memos by mutableStateOf((1L..100L).map { Memo(categoryId = 1L, id = it) })

    private val bottomKey get() = listState.layoutInfo.visibleItemsInfo.first().key

    private fun setFeed() = rule.setContent { MemoFeed(memos = memos, listState = listState) }

    private fun scrollTo(index: Int) = rule.runOnIdle { runBlocking { listState.scrollToItem(index) } }

    /** 맨 아래에 있을 때 새 메모가 추가되면, 맨 아래에 그 새 메모가 보인다. */
    @Test
    fun atBottom_newMemo_showsNewMemo() {
        setFeed()

        memos = memos + Memo(categoryId = 1L, id = 101L)

        rule.runOnIdle {
            assertEquals(0, listState.firstVisibleItemIndex)
            assertEquals(0, listState.firstVisibleItemScrollOffset)
            assertEquals(101L, bottomKey)
        }
    }

    /** 위로 스크롤해 둔 상태에서 새 메모가 추가되면, 보고 있던 메모가 그대로 맨 아래에 남는다. */
    @Test
    fun scrolled_newMemo_keepsPosition() {
        setFeed()
        scrollTo(10)
        val before = rule.runOnIdle { bottomKey }

        memos = memos + Memo(categoryId = 1L, id = 101L)

        rule.runOnIdle { assertEquals(before, bottomKey) }
    }

    /** 위로 스크롤해 둔 상태에서 화면 밖의 최신 메모가 삭제돼도, 보고 있던 메모가 그대로 맨 아래에 남는다. */
    @Test
    fun scrolled_deleteNewestMemo_keepsPosition() {
        setFeed()
        scrollTo(10)
        val before = rule.runOnIdle { bottomKey }

        memos = memos.filter { it.id != 100L }

        rule.runOnIdle { assertEquals(before, bottomKey) }
    }
}
