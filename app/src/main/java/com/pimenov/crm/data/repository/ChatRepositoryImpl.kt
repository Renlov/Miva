package com.pimenov.crm.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.usecase.GetChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.InsertChatMessageUseCase
import com.pimenov.crm.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val insertChatMessage: InsertChatMessageUseCase,
    private val getChatMessages: GetChatMessagesUseCase
) : ChatRepository {

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.0-flash")

    override suspend fun sendMessage(conversationId: Long, userMessage: String): Result<ChatMessage> {
        val userMsg = ChatMessage(
            conversationId = conversationId,
            role = "user",
            content = userMessage
        )
        insertChatMessage(userMsg)

        return runCatching {
            val allMessages = getChatMessages(conversationId)
            val history = allMessages.dropLast(1).map { msg ->
                val role = if (msg.role == "user") "user" else "model"
                content(role = role) { text(msg.content) }
            }

            val chat = model.startChat(history = history)
            val response = chat.sendMessage(userMessage)

            val assistantContent = response.text ?: "Нет ответа"

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
