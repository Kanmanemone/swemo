package com.example.database.model

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val position: Int = 0,
)

fun CategoryEntity.asExternalModel(): Category = Category(
    id = id,
    name = name
)
