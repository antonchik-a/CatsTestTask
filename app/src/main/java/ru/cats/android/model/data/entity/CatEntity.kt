package ru.cats.android.model.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cats")
data class CatEntity(
    @PrimaryKey val id: String,
    val height: Int,
    val url: String,
    val width: Int
)