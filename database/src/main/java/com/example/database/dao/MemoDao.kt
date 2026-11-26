package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.model.MemoContentEntity
import com.example.database.model.MemoEntity
import com.example.database.model.PopulatedMemo
import com.example.model.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    // 조합 작업 CRUD (Populated CRUD)
    @Transaction
    @Query("SELECT * FROM memos ORDER BY categoryId, position, id")
    fun getPopulatedMemos(): Flow<List<PopulatedMemo>>

    @Transaction
    @Query("SELECT * FROM memos WHERE categoryId = :categoryId ORDER BY position, id")
    fun getPopulatedMemosByCategoryId(categoryId: Long): Flow<List<PopulatedMemo>>

    @Transaction
    suspend fun insertPopulatedMemo(memo: Memo): Long {
        val position = getNextMemoPosition(memo.categoryId)
        val memoId = insertMemo(memo.asMemoEntity(position))
        insertMemoContents(memo.asMemoContentEntities(memoId))
        return memoId
    }

    @Transaction
    suspend fun updatePopulatedMemo(memo: Memo) {
        updateMemo(memo.id, memo.categoryId)
        deleteMemoContentsByMemoId(memo.id)
        insertMemoContents(memo.asMemoContentEntities(memo.id))
    }

    // 옮긴 카테고리의 맨 끝에 둔다. 원래 position을 그대로 두면 새 카테고리의 다른 메모 사이에 끼어든다
    @Transaction
    suspend fun updateMemoCategory(memoId: Long, categoryId: Long) {
        updateMemoCategoryAndPosition(memoId, categoryId, getNextMemoPosition(categoryId))
    }

    @Transaction
    suspend fun deletePopulatedMemo(memoId: Long): Long? {
        deleteMemoContentsByMemoId(memoId)
        val targetMemoExists = deleteMemo(memoId) > 0
        return if (targetMemoExists) memoId else null
    }

    @Transaction
    suspend fun updateMemoPositions(memoIdsInOrder: List<Long>) {
        memoIdsInOrder.forEachIndexed { position, memoId ->
            updateMemoPosition(memoId, position)
        }
    }


    // 기본 CRUD (Basic CRUD)
    @Insert
    suspend fun insertMemo(memo: MemoEntity): Long

    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM memos WHERE categoryId = :categoryId")
    suspend fun getNextMemoPosition(categoryId: Long): Int

    @Insert
    suspend fun insertMemoContents(contents: List<MemoContentEntity>)

    @Query("UPDATE memos SET categoryId = :categoryId WHERE id = :memoId")
    suspend fun updateMemo(memoId: Long, categoryId: Long)

    @Query("UPDATE memos SET categoryId = :categoryId, position = :position WHERE id = :memoId")
    suspend fun updateMemoCategoryAndPosition(memoId: Long, categoryId: Long, position: Int)

    @Query("UPDATE memos SET position = :position WHERE id = :memoId")
    suspend fun updateMemoPosition(memoId: Long, position: Int)

    @Query("DELETE FROM memos WHERE id = :memoId")
    suspend fun deleteMemo(memoId: Long): Int

    @Query("DELETE FROM memo_contents WHERE id = :memoContentId")
    suspend fun deleteMemoContent(memoContentId: Long)

    @Query("DELETE FROM memo_contents WHERE memoId = :memoId")
    suspend fun deleteMemoContentsByMemoId(memoId: Long)
}

private fun Memo.asMemoEntity(position: Int = 0): MemoEntity = MemoEntity(
    id = id.takeIf { it > 0L } ?: 0L,
    categoryId = categoryId,
    position = position,
)

private fun Memo.asMemoContentEntities(memoId: Long): List<MemoContentEntity> =
    contents.mapIndexed { index, content ->
        MemoContentEntity(
            id = content.id.takeIf { it > 0L } ?: 0L,
            memoId = memoId,
            position = index,
            label = content.label,
            text = content.text
        )
    }
