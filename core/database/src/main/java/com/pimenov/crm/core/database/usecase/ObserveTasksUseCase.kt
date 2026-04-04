package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.Task
import com.pimenov.crm.core.database.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.observeAll()
}
