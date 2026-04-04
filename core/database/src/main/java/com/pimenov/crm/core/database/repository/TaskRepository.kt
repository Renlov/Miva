package com.pimenov.crm.core.database.repository

import com.pimenov.crm.core.database.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    suspend fun save(task: Task): Long
    suspend fun toggleDone(id: Long)
    suspend fun delete(id: Long)
    suspend fun deleteAll()
}
