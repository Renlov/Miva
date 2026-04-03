package com.pimenov.crm.domain.repository

import com.pimenov.crm.domain.model.AppSettings
import com.pimenov.crm.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setApiKey(key: String)
}
