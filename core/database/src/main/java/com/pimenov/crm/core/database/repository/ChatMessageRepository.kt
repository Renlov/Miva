package com.pimenov.crm.core.database.repository

import com.pimenov.crm.core.database.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatMessageRepository {
    fun observeMessages(conversationId: Long): Flow<List<ChatMessage>>
    fun observeConversations(): Flow<List<Long>>
    suspend fun getMessages(conversationId: Long): List<ChatMessage>
    suspend fun insert(message: ChatMessage): Long
    suspend fun deleteConversation(conversationId: Long)
    suspend fun deleteAll()
    suspend fun nextConversationId(): Long
}
