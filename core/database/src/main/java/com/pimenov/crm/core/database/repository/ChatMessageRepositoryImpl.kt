package com.pimenov.crm.core.database.repository

import com.pimenov.crm.core.database.dao.ChatMessageDao
import com.pimenov.crm.core.database.entity.ChatMessageEntity
import com.pimenov.crm.core.database.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ChatMessageRepositoryImpl(private val dao: ChatMessageDao) : ChatMessageRepository {

    override fun observeMessages(conversationId: Long): Flow<List<ChatMessage>> =
        dao.observeMessages(conversationId).map { list -> list.map { it.toDomain() } }

    override fun observeConversations(): Flow<List<Long>> =
        dao.observeConversations()

    override suspend fun getMessages(conversationId: Long): List<ChatMessage> =
        dao.getMessages(conversationId).map { it.toDomain() }

    override suspend fun insert(message: ChatMessage): Long =
        dao.insert(ChatMessageEntity.fromDomain(message))

    override suspend fun deleteConversation(conversationId: Long) =
        dao.deleteConversation(conversationId)

    override suspend fun deleteAll() = dao.deleteAll()

    override suspend fun nextConversationId(): Long = dao.nextConversationId()
}
