package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.database.model.MemoContentEntity
import com.example.database.model.MemoEntity
import com.example.database.model.PopulatedMemo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Transaction
    @Query("SELECT * FROM memos ORDER BY id")
    fun getPopulatedMemos(): Flow<List<PopulatedMemo>>

    @Transaction
    @Query("SELECT * FROM memos WHERE categoryId = :categoryId ORDER BY id")
    fun getPopulatedMemosByCategory(categoryId: Long): Flow<List<PopulatedMemo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: MemoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoContents(contents: List<MemoContentEntity>)

    @Transaction
    suspend fun insertMemoWithContents(
        memo: MemoEntity,
        contents: List<MemoContentEntity>
    ) {
        val memoId = insertMemo(memo)
        insertMemoContents(
            contents.mapIndexed { index, content ->
                content.copy(
                    id = 0L,
                    memoId = memoId,
                    position = index
                )
            }
        )
    }
}