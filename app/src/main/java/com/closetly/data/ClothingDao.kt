package com.closetly.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class CategoryColorCount(
    val category: String,
    val color: String,
    val c: Int
)

@Dao
interface ClothingDao {

    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ClothingItem>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ClothingItem?

    @Query("SELECT COUNT(*) FROM items")
    fun observeCount(): Flow<Int>

    @Query("SELECT category, color, COUNT(*) as c FROM items GROUP BY category, color HAVING c > 1 ORDER BY c DESC")
    fun observeDuplicates(): Flow<List<CategoryColorCount>>

    @Query("SELECT * FROM items ORDER BY wornCount DESC, lastWornAt DESC LIMIT :limit")
    fun observeTopWorn(limit: Int): Flow<List<ClothingItem>>

    @Query("SELECT * FROM items WHERE lastWornAt IS NULL OR lastWornAt < :before ORDER BY COALESCE(lastWornAt, 0) ASC LIMIT :limit")
    fun observeIdle(before: Long, limit: Int): Flow<List<ClothingItem>>

    @Insert
    suspend fun insert(item: ClothingItem): Long

    @Update
    suspend fun update(item: ClothingItem)

    @Delete
    suspend fun delete(item: ClothingItem)
}
