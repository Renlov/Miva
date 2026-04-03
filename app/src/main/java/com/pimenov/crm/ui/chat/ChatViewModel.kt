package com.pimenov.crm.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.domain.model.ChatMessage
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

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = _state.flatMapLatest { s ->
        repository.observeMessages(s.conversationId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val conversations = repository.observeConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val convId = _state.value.conversationId
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.sendMessage(convId, text)
            _state.value = _state.value.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun newConversation() {
        viewModelScope.launch {
            val id = repository.newConversationId()
            _state.value = _state.value.copy(conversationId = id, error = null)
        }
    }

    fun selectConversation(id: Long) {
        _state.value = _state.value.copy(conversationId = id, error = null)
    }
}
