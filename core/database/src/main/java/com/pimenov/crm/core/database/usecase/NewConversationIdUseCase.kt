package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.ChatMessageRepository

class NewConversationIdUseCase(private val repository: ChatMessageRepository) {
    suspend operator fun invoke(): Long = repository.nextConversationId()
}
