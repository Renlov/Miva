package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.TaskRepository

class ToggleTaskDoneUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Long) = repository.toggleDone(id)
}
