package com.pimenov.crm.domain.repository

import com.pimenov.crm.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(conversationId: Long): Flow<List<ChatMessage>>
    fun observeConversations(): Flow<List<Long>>
    suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage>
    suspend fun deleteConversation(conversationId: Long)
    suspend fun deleteAll()
    suspend fun newConversationId(): Long
}
