package com.pimenov.crm.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.core.database.usecase.NewConversationIdUseCase
import com.pimenov.crm.core.database.usecase.ObserveChatMessagesUseCase
import com.pimenov.crm.core.database.usecase.ObserveConversationsUseCase
import com.pimenov.crm.domain.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatState(
    val conversationId: Long = 1L,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    observeChatMessages: ObserveChatMessagesUseCase,
    observeConversations: ObserveConversationsUseCase,
    private val newConversationId: NewConversationIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = _state.flatMapLatest { s ->
        observeChatMessages(s.conversationId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val conversations = observeConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val convId = _state.value.conversationId
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = chatRepository.sendMessage(convId, text)
            _state.value = _state.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun newConversation() {
        viewModelScope.launch {
            val id = newConversationId()
            _state.value = _state.value.copy(conversationId = id, error = null)
        }
    }

    fun selectConversation(id: Long) {
        _state.value = _state.value.copy(conversationId = id, error = null)
    }
}
