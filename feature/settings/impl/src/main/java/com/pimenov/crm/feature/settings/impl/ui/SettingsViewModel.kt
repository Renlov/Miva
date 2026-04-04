package com.pimenov.crm.feature.settings.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pimenov.crm.feature.settings.impl.data.AppLanguage
import com.pimenov.crm.feature.settings.impl.data.SettingsPreferences
import com.pimenov.crm.feature.settings.impl.data.SettingsState
import com.pimenov.crm.feature.settings.impl.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefs: SettingsPreferences
) : ViewModel() {

    val settings = prefs.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { prefs.setThemeMode(mode) }
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch { prefs.setLanguage(lang) }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch { prefs.setApiKey(key) }
    }

    fun setAiDailyLimit(limit: Int) {
        viewModelScope.launch { prefs.setAiDailyLimit(limit) }
    }

    fun setAiModel(model: String) {
        viewModelScope.launch { prefs.setAiModel(model) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    fun setNotifyOnTaskDue(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotifyOnTaskDue(enabled) }
    }

    fun setNotifyOnAiReply(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotifyOnAiReply(enabled) }
    }
}
