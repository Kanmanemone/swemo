package com.example.memo.components

import org.junit.Assert.assertEquals
import org.junit.Test

class DragReorderTest {

    private val items = listOf("a", "b", "c", "d")

    @Test
    fun moved_down() {
        assertEquals(listOf("b", "c", "a", "d"), items.moved(from = 0, to = 2))
    }

    @Test
    fun moved_up() {
        assertEquals(listOf("a", "d", "b", "c"), items.moved(from = 3, to = 1))
    }

    @Test
    fun moved_samePosition_keepsOrder() {
        assertEquals(items, items.moved(from = 2, to = 2))
    }

    @Test
    fun moved_toEdges() {
        assertEquals(listOf("b", "c", "d", "a"), items.moved(from = 0, to = 3))
        assertEquals(listOf("d", "a", "b", "c"), items.moved(from = 3, to = 0))
    }

    @Test
    fun moved_leavesOriginalUntouched() {
        items.moved(from = 0, to = 3)
        assertEquals(listOf("a", "b", "c", "d"), items)
    }
}
