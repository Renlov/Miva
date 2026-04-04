package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.Task
import com.pimenov.crm.core.database.repository.TaskRepository

class SaveTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Long = repository.save(task)
}
