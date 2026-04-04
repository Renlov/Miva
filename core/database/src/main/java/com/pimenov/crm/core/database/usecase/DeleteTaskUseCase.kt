package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.TaskRepository

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}
