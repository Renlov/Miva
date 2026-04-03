package com.pimenov.crm.data.repository

import com.pimenov.crm.data.local.dao.ChatMessageDao
import com.pimenov.crm.data.local.entity.ChatMessageEntity
import com.pimenov.crm.data.remote.AiApiService
import com.pimenov.crm.data.remote.dto.ChatRequestBody
import com.pimenov.crm.data.remote.dto.MessageDto
import com.pimenov.crm.domain.model.ChatMessage
import com.pimenov.crm.domain.repository.ChatRepository
import com.pimenov.crm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val dao: ChatMessageDao,
    private val api: AiApiService,
    private val settingsRepository: SettingsRepository
) : ChatRepository {

    override fun observeMessages(conversationId: Long): Flow<List<ChatMessage>> =
        dao.observeMessages(conversationId).map { list -> list.map { it.toDomain() } }

    override fun observeConversations(): Flow<List<Long>> =
        dao.observeConversations()

    override suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage> {
        val userEntity = ChatMessageEntity(
            conversationId = conversationId,
            role = "user",
            content = userMessage
        )
        dao.insert(userEntity)

        return runCatching {
            val apiKey = settingsRepository.observeSettings().first().apiKey
            val history = dao.getMessages(conversationId).map {
                MessageDto(role = it.role, content = it.content)
            }

            val response = api.chatCompletion(
                auth = "Bearer $apiKey",
                body = ChatRequestBody(messages = history)
            )

            val assistantContent = response.choices.firstOrNull()?.message?.content
                ?: "Нет ответа"

            val assistantEntity = ChatMessageEntity(
                conversationId = conversationId,
                role = "assistant",
                content = assistantContent
            )
            dao.insert(assistantEntity)
            assistantEntity.toDomain()
        }
    }

    override suspend fun deleteConversation(conversationId: Long) =
        dao.deleteConversation(conversationId)

    override suspend fun deleteAll() = dao.deleteAll()

    override suspend fun newConversationId(): Long = dao.nextConversationId()
}
