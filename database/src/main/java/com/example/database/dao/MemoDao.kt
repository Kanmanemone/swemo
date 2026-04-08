package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.database.model.MemoContentEntity
import com.example.database.model.MemoEntity
import com.example.database.model.PopulatedMemo
import com.example.model.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    // 조합 작업 CRUD (Populated CRUD)
    @Transaction
    @Query("SELECT * FROM memos ORDER BY id")
    fun getPopulatedMemos(): Flow<List<PopulatedMemo>>

    @Transaction
    @Query("SELECT * FROM memos WHERE categoryId = :categoryId ORDER BY id")
    fun getPopulatedMemosByCategoryId(categoryId: Long): Flow<List<PopulatedMemo>>

    @Transaction
    suspend fun insertPopulatedMemo(memo: Memo): Long {
        val memoId = insertMemo(memo.asMemoEntity())
        insertMemoContents(memo.asMemoContentEntities(memoId))
        return memoId
    }

    @Transaction
    suspend fun updatePopulatedMemo(memo: Memo) {
        updateMemo(memo.asMemoEntity())
        deleteMemoContentsByMemoId(memo.id)
        insertMemoContents(memo.asMemoContentEntities(memo.id))
    }

    @Transaction
    suspend fun deletePopulatedMemo(memoId: Long): Long? {
        deleteMemoContentsByMemoId(memoId)
        val targetMemoExists = deleteMemo(memoId) > 0
        return if (targetMemoExists) memoId else null
    }


    // 기본 CRUD (Basic CRUD)
    @Insert
    suspend fun insertMemo(memo: MemoEntity): Long

    @Insert
    suspend fun insertMemoContents(contents: List<MemoContentEntity>)

    @Update
    suspend fun updateMemo(memo: MemoEntity)

    @Query("DELETE FROM memos WHERE id = :memoId")
    suspend fun deleteMemo(memoId: Long): Int

    @Query("DELETE FROM memo_contents WHERE id = :memoContentId")
    suspend fun deleteMemoContent(memoContentId: Long)

    @Query("DELETE FROM memo_contents WHERE memoId = :memoId")
    suspend fun deleteMemoContentsByMemoId(memoId: Long)
}

private fun Memo.asMemoEntity(): MemoEntity = MemoEntity(
    id = id.takeIf { it > 0L } ?: 0L,
    categoryId = categoryId
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