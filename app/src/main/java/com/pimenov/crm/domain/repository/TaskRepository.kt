package com.pimenov.crm.domain.repository

import com.pimenov.crm.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    suspend fun save(task: Task): Long
    suspend fun toggleDone(id: Long)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}
