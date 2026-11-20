package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY position, id")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategory(categoryId: Long): CategoryEntity?

    @Transaction
    suspend fun insertCategory(category: CategoryEntity): Long =
        insertCategoryInternal(category.copy(position = getNextCategoryPosition()))

    @Insert
    suspend fun insertCategoryInternal(category: CategoryEntity): Long

    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM categories")
    suspend fun getNextCategoryPosition(): Int

    @Query("UPDATE categories SET name = :name WHERE id = :categoryId")
    suspend fun updateCategoryName(categoryId: Long, name: String)

    @Transaction
    suspend fun updateCategoryPositions(categoryIdsInOrder: List<Long>) {
        categoryIdsInOrder.forEachIndexed { position, categoryId ->
            updateCategoryPosition(categoryId, position)
        }
    }

    @Query("UPDATE categories SET position = :position WHERE id = :categoryId")
    suspend fun updateCategoryPosition(categoryId: Long, position: Int)

    @Transaction
    suspend fun deleteCategory(categoryId: Long): Long? {
        val deleted = deleteCategoryInternal(categoryId) > 0
        return if (deleted) categoryId else null
    }

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryInternal(categoryId: Long): Int
}
