package com.closetly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // required
    val imagePath: String,
    val category: String,
    val color: String, // 多个颜色标签用逗号分隔，例如：黑,白,灰

    // optional
    val purchaseAt: Long? = null,
    val material: String? = null,
    val brand: String? = null,
    val size: String? = null,
    val price: Double? = null,
    val note: String? = null,

    // meta
    val createdAt: Long = System.currentTimeMillis(),
    val lastWornAt: Long? = null,
    val wornCount: Int = 0
)
