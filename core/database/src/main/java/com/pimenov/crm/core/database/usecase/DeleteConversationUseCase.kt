package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.ChatMessageRepository

class DeleteConversationUseCase(private val repository: ChatMessageRepository) {
    suspend operator fun invoke(conversationId: Long) =
        repository.deleteConversation(conversationId)
}
