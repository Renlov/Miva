package com.pimenov.crm.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pimenov.crm.core.database.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderAt: Long? = null,
    val sortOrder: Int = 0
) {
    fun toDomain(): Task = Task(id, title, isDone, createdAt, reminderAt, sortOrder)

    companion object {
        fun fromDomain(task: Task): TaskEntity =
            TaskEntity(task.id, task.title, task.isDone, task.createdAt, task.reminderAt, task.sortOrder)
    }
}
