package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.CategoryDao
import com.example.database.dao.MemoDao
import com.example.database.model.CategoryEntity
import com.example.database.model.MemoContentEntity
import com.example.database.model.MemoEntity

@Database(
    entities = [
        CategoryEntity::class,
        MemoEntity::class,
        MemoContentEntity::class,
    ],
    version = 1,
    exportSchema = true
)
internal abstract class SwemoDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun memoDao(): MemoDao
}