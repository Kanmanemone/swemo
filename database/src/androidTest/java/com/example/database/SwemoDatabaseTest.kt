package com.example.database

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.database.model.CategoryEntity
import com.example.model.Memo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwemoDatabaseTest {
    @get:Rule
    val migrationHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SwemoDatabase::class.java,
    )

    private var database: SwemoDatabase? = null

    @After
    fun closeDatabase() {
        database?.close()
    }

    @Test
    fun migration1To2_preservesIdOrderAsPosition() {
        migrationHelper.createDatabase(TEST_DATABASE, 1).apply {
            execSQL("INSERT INTO categories (id, name) VALUES (10, 'first'), (20, 'second')")
            execSQL("INSERT INTO memos (id, categoryId) VALUES (30, 10), (40, 10), (50, 20)")
            close()
        }

        migrationHelper.runMigrationsAndValidate(TEST_DATABASE, 2, true, MIGRATION_1_2).apply {
            query("SELECT id, position FROM categories ORDER BY id").use { cursor ->
                cursor.moveToFirst()
                assertEquals(10L, cursor.getLong(0))
                assertEquals(0, cursor.getInt(1))
                cursor.moveToNext()
                assertEquals(20L, cursor.getLong(0))
                assertEquals(1, cursor.getInt(1))
            }
            query("SELECT id, position FROM memos ORDER BY id").use { cursor ->
                cursor.moveToFirst()
                assertEquals(30L, cursor.getLong(0))
                assertEquals(0, cursor.getInt(1))
                cursor.moveToNext()
                assertEquals(40L, cursor.getLong(0))
                assertEquals(1, cursor.getInt(1))
                cursor.moveToNext()
                assertEquals(50L, cursor.getLong(0))
                assertEquals(0, cursor.getInt(1))
            }
            close()
        }
    }

    @Test
    fun daos_appendAndReorderCategoriesAndMemos() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(),
            SwemoDatabase::class.java,
        ).build()
        val categoryDao = database!!.categoryDao()
        val memoDao = database!!.memoDao()

        val firstCategoryId = categoryDao.insertCategory(CategoryEntity(name = "first"))
        val secondCategoryId = categoryDao.insertCategory(CategoryEntity(name = "second"))
        categoryDao.updateCategoryPositions(listOf(secondCategoryId, firstCategoryId))

        val firstMemoId = memoDao.insertPopulatedMemo(Memo(categoryId = firstCategoryId, id = 0L))
        val secondMemoId = memoDao.insertPopulatedMemo(Memo(categoryId = firstCategoryId, id = 0L))
        memoDao.updateMemoPositions(listOf(secondMemoId, firstMemoId))

        assertEquals(
            listOf(secondCategoryId, firstCategoryId),
            categoryDao.getCategories().first().map(CategoryEntity::id),
        )
        assertEquals(
            listOf(secondMemoId, firstMemoId),
            memoDao.getPopulatedMemosByCategoryId(firstCategoryId).first().map { it.entity.id },
        )
    }

    private companion object {
        const val TEST_DATABASE = "migration-test"
    }
}
