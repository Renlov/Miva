package com.pimenov.crm.core.database.model

data class Task(
    val id: Long = 0,
    val title: String = "",
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderAt: Long? = null,
    val sortOrder: Int = 0
)
