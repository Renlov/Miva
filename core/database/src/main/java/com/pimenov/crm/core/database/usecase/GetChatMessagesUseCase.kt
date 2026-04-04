package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.repository.ChatMessageRepository

class GetChatMessagesUseCase(private val repository: ChatMessageRepository) {
    suspend operator fun invoke(conversationId: Long): List<ChatMessage> =
        repository.getMessages(conversationId)
}
