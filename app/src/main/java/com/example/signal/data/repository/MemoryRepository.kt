package com.example.signal.data.repository

import com.example.signal.data.local.MemoryDao
import com.example.signal.data.local.MemoryEntity
import kotlinx.coroutines.flow.Flow

class MemoryRepository(
    private val memoryDao: MemoryDao
) {

    fun getAllMemories(): Flow<List<MemoryEntity>> {
        return memoryDao.getAllMemories()
    }

    fun searchMemories(query: String): Flow<List<MemoryEntity>> {
        return memoryDao.searchMemories(query)
    }

    suspend fun getMemoryById(id: Long): MemoryEntity? {
        return memoryDao.getMemoryById(id)
    }

    suspend fun insert(memory: MemoryEntity) {
        memoryDao.insert(memory)
    }

    suspend fun delete(memory: MemoryEntity) {
        memoryDao.delete(memory)
    }
}