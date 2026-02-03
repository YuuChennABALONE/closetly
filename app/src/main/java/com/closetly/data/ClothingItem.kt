package com.closetly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val category: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastWornAt: Long? = null,
    val wornCount: Int = 0
)
