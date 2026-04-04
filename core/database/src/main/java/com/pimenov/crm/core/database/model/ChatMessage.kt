package com.pimenov.crm.core.database.model

data class ChatMessage(
    val id: Long = 0,
    val conversationId: Long = 0,
    val role: String = "user",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
