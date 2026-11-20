package com.example.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeMemoRepositoryImplTest {
    @Test
    fun updatePositions_reordersOnlyRequestedItems() = runBlocking {
        val repository = FakeMemoRepositoryImpl()

        repository.updateCategoryPositions(listOf(3L, 2L, 1L))
        repository.updateMemoPositions(listOf(4L, 3L))

        assertEquals(listOf(3L, 2L, 1L), repository.getCategories().first().map { it.id })
        assertEquals(listOf(1L, 2L, 4L, 3L, 5L, 6L), repository.getMemos().first().map { it.id })
    }
}
