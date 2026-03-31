package com.example.data

import com.example.database.dao.CategoryDao
import com.example.database.dao.MemoDao
import com.example.database.model.CategoryEntity
import com.example.database.model.MemoContentEntity
import com.example.database.model.MemoEntity
import com.example.database.model.PopulatedMemo
import com.example.database.model.asExternalModel
import com.example.model.Category
import com.example.model.Memo
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Reusable
class MemoRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val memoDao: MemoDao
) : MemoRepository {

    override fun getCategory(): Flow<List<Category>> = flow {
        seedDefaultCategoriesIfEmpty()
        emitAll(
            categoryDao.getCategoryEntities().map { categories ->
                categories.map(CategoryEntity::asExternalModel)
            }
        )
    }

    override fun getMemos(): Flow<List<Memo>> =
        memoDao.getPopulatedMemos().map { memos ->
            memos.map(PopulatedMemo::asExternalModel)
        }

    override fun getMemosByCategory(categoryId: Long): Flow<List<Memo>> =
        memoDao.getPopulatedMemosByCategory(categoryId).map { memos ->
            memos.map(PopulatedMemo::asExternalModel)
        }

    override suspend fun insertMemo(memo: Memo) {
        memoDao.insertMemoWithContents(
            memo = MemoEntity(
                id = memo.id.takeIf { it > 0L } ?: 0L,
                categoryId = memo.categoryId
            ),
            contents = memo.contents.mapIndexed { index, content ->
                MemoContentEntity(
                    id = content.id.takeIf { it > 0L } ?: 0L,
                    memoId = memo.id,
                    position = index,
                    label = content.label,
                    text = content.text
                )
            }
        )
    }

    private suspend fun seedDefaultCategoriesIfEmpty() {
        if (categoryDao.getCategoryEntities().first().isNotEmpty()) return

        DefaultCategories.forEach { category ->
            categoryDao.insertCategory(category)
        }
    }
}

private val DefaultCategories = listOf(
    CategoryEntity(id = 1L, name = "category 1"),
    CategoryEntity(id = 2L, name = "category 2"),
    CategoryEntity(id = 3L, name = "category 3")
)