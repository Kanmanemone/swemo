package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("UPDATE categories SET name = :name WHERE id = :categoryId")
    suspend fun updateCategoryName(categoryId: Long, name: String)

    @Transaction
    suspend fun deleteCategory(categoryId: Long): Long? {
        val deleted = deleteCategoryInternal(categoryId) > 0
        return if (deleted) categoryId else null
    }

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryInternal(categoryId: Long): Int
}