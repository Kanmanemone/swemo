package com.example.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.model.Memo

data class PopulatedMemo(
    @Embedded val entity: MemoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "memoId"
    )
    val contents: List<MemoContentEntity>
)

fun PopulatedMemo.asExternalModel(): Memo = Memo(
    categoryId = entity.categoryId,
    id = entity.id,
    contents = contents
        .sortedBy(MemoContentEntity::position)
        .map(MemoContentEntity::asExternalModel)
)