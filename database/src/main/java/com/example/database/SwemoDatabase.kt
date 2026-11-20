package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 2,
    exportSchema = true
)
internal abstract class SwemoDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun memoDao(): MemoDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE categories ADD COLUMN position INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            UPDATE categories
            SET position = (
                SELECT COUNT(*) FROM categories AS preceding
                WHERE preceding.id < categories.id
            )
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE memos ADD COLUMN position INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            UPDATE memos
            SET position = (
                SELECT COUNT(*) FROM memos AS preceding
                WHERE preceding.categoryId = memos.categoryId
                    AND preceding.id < memos.id
            )
            """.trimIndent()
        )
    }
}
