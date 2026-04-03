package com.pimenov.crm.data.repository

import com.pimenov.crm.data.local.dao.TaskDao
import com.pimenov.crm.data.local.entity.TaskEntity
import com.pimenov.crm.domain.model.Task
import com.pimenov.crm.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun observeAll(): Flow<List<Task>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun save(task: Task): Long =
        dao.insert(TaskEntity.fromDomain(task))

    override suspend fun toggleDone(id: Long) = dao.toggleDone(id)

    override suspend fun delete(id: Long) = dao.delete(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
