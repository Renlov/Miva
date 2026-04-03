package com.pimenov.crm.data.repository

import com.pimenov.crm.data.preferences.AppPreferences
import com.pimenov.crm.domain.model.AppSettings
import com.pimenov.crm.domain.model.ThemeMode
import com.pimenov.crm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(private val prefs: AppPreferences) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = prefs.observeSettings()

    override suspend fun setThemeMode(mode: ThemeMode) = prefs.setThemeMode(mode)

    override suspend fun setApiKey(key: String) = prefs.setApiKey(key)
}
