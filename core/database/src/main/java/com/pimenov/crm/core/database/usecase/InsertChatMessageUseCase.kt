package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.model.ChatMessage
import com.pimenov.crm.core.database.repository.ChatMessageRepository

class InsertChatMessageUseCase(private val repository: ChatMessageRepository) {
    suspend operator fun invoke(message: ChatMessage): Long = repository.insert(message)
}
