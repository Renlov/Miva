package com.pimenov.crm.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.domain.model.AppSettings
import com.pimenov.crm.domain.model.ThemeMode
import com.pimenov.crm.domain.repository.ChatRepository
import com.pimenov.crm.domain.repository.NoteRepository
import com.pimenov.crm.domain.repository.SettingsRepository
import com.pimenov.crm.domain.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val noteRepository: NoteRepository,
    private val chatRepository: ChatRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val settings = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch { settingsRepository.setApiKey(key) }
    }

    fun clearNotes() {
        viewModelScope.launch { noteRepository.deleteAll() }
    }

    fun clearChats() {
        viewModelScope.launch { chatRepository.deleteAll() }
    }

    fun clearTasks() {
        viewModelScope.launch { taskRepository.deleteAll() }
    }
}
