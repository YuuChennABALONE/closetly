package com.closetly.data

import kotlinx.coroutines.flow.Flow

class ClothingRepository(private val dao: ClothingDao) {

    fun observeAll(): Flow<List<ClothingItem>> = dao.observeAll()
    fun observeCount(): Flow<Int> = dao.observeCount()
    fun observeDuplicates(): Flow<List<CategoryColorCount>> = dao.observeDuplicates()
    fun observeTopWorn(limit: Int): Flow<List<ClothingItem>> = dao.observeTopWorn(limit)
    fun observeIdle(before: Long, limit: Int): Flow<List<ClothingItem>> = dao.observeIdle(before, limit)

    suspend fun add(item: ClothingItem): Long = dao.insert(item)
    suspend fun update(item: ClothingItem) = dao.update(item)
}
