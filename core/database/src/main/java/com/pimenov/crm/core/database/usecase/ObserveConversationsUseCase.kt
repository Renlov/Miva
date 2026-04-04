package com.pimenov.crm.core.database.usecase

import com.pimenov.crm.core.database.repository.ChatMessageRepository
import kotlinx.coroutines.flow.Flow

class ObserveConversationsUseCase(private val repository: ChatMessageRepository) {
    operator fun invoke(): Flow<List<Long>> = repository.observeConversations()
}
