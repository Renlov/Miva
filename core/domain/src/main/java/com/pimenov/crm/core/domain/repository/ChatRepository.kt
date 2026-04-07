package com.pimenov.crm.core.domain.repository

import com.pimenov.crm.core.database.model.ChatMessage

interface ChatRepository {
    suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage>
}
