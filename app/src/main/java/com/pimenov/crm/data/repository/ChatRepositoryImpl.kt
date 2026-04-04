package com.pimenov.crm.data.repository

import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.usecase.DeleteConversationUseCase
import com.pimenov.crm.core.database.usecase.GetChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.InsertChatMessageUseCase
import com.pimenov.crm.core.database.usecase.NewConversationIdUseCase
import com.pimenov.crm.core.database.usecase.ObserveChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.ObserveConversationsUseCase
import com.pimenov.crm.data.remote.AiApiService
import com.pimenov.crm.data.remote.dto.ChatRequestBody
import com.pimenov.crm.data.remote.dto.MessageDto
import com.pimenov.crm.domain.repository.ChatRepository
import com.pimenov.crm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ChatRepositoryImpl(
    private val insertChatMessage: InsertChatMessageUseCase,
    private val getChatMessages: GetChatMessagesUseCase,
    private val api: AiApiService,
    private val settingsRepository: SettingsRepository
) : ChatRepository {

    override suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage> {
        val userMsg = ChatMessage(
            conversationId = conversationId,
            role = "user",
            content = userMessage
        )
        insertChatMessage(userMsg)

        return runCatching {
            val apiKey = settingsRepository.observeSettings().first().apiKey
            val history = getChatMessages(conversationId).map {
                MessageDto(role = it.role, content = it.content)
            }

            val response = api.chatCompletion(
                auth = "Bearer $apiKey",
                body = ChatRequestBody(messages = history)
            )

            val assistantContent = response.choices.firstOrNull()?.message?.content
                ?: "Нет ответа"

            val assistantMsg = ChatMessage(
                conversationId = conversationId,
                role = "assistant",
                content = assistantContent
            )
            insertChatMessage(assistantMsg)
            assistantMsg
        }
    }
}
