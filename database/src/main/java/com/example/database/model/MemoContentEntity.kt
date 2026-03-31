package com.example.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.MemoContent

@Entity(tableName = "memo_contents")
data class MemoContentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val memoId: Long,
    val position: Int,
    val label: String,
    val text: String
)

fun MemoContentEntity.asExternalModel(): MemoContent = MemoContent(
    id = id,
    label = label,
    text = text
)