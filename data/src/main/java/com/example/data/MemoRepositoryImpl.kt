package com.example.data

import com.example.database.dao.CategoryDao
import com.example.database.dao.MemoDao
import com.example.database.model.CategoryEntity
import com.example.database.model.PopulatedMemo
import com.example.database.model.asExternalModel
import com.example.model.Category
import com.example.model.Memo
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Reusable
class MemoRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val memoDao: MemoDao
) : MemoRepository {

    override fun getCategory(): Flow<List<Category>> =
        categoryDao.getCategories().map { categories ->
            categories.map(CategoryEntity::asExternalModel)
        }

    override fun getMemos(): Flow<List<Memo>> =
        memoDao.getPopulatedMemos().map { memos ->
            memos.map(PopulatedMemo::asExternalModel)
        }

    override fun getMemosByCategory(categoryId: Long): Flow<List<Memo>> =
        memoDao.getPopulatedMemosByCategoryId(categoryId).map { memos ->
            memos.map(PopulatedMemo::asExternalModel)
        }

    override suspend fun insertCategory(name: String): Long {
        val trimmedName = name.trim()
        return categoryDao.insertCategory(CategoryEntity(name = trimmedName))
    }

    override suspend fun insertMemo(memo: Memo): Long {
        return memoDao.insertPopulatedMemo(memo)
    }

    override suspend fun updateCategoryName(categoryId: Long, name: String) {
        categoryDao.updateCategoryName(categoryId, name)
    }

    override suspend fun updateMemo(memo: Memo) {
        memoDao.updatePopulatedMemo(memo)
    }

    override suspend fun deleteCategory(categoryId: Long): Long? {
        return categoryDao.deleteCategory(categoryId)
    }

    override suspend fun deleteMemo(memoId: Long): Long? {
        return memoDao.deletePopulatedMemo(memoId)
    }

    override suspend fun deleteMemoContent(memoContentId: Long) {
        memoDao.deleteMemoContent(memoContentId)
    }
}