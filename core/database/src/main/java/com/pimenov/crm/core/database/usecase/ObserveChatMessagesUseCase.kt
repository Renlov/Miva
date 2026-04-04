package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.repository.ChatMessageRepository
import kotlinx.coroutines.flow.Flow

class ObserveChatMessagesUseCase(private val repository: ChatMessageRepository) {
    operator fun invoke(conversationId: Long): Flow<List<ChatMessage>> =
        repository.observeMessages(conversationId)
}
