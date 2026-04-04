package com.pimenov.crm.domain.repository

import com.pimenov.crm.core.database.model.ChatMessage

interface ChatRepository {
    suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage>
}
