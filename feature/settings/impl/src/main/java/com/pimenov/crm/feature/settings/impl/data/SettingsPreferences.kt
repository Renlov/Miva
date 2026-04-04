package com.pimenov.crm.feature.settings.impl.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "feature_settings"
)

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val apiKey: String = "",
    val aiDailyLimit: Int = 50,
    val aiModel: String = "gpt-4o-mini",
    val notificationsEnabled: Boolean = true,
    val notifyOnTaskDue: Boolean = true,
    val notifyOnAiReply: Boolean = true
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class AppLanguage { SYSTEM, RU, EN }

class SettingsPreferences(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_mode")
    private val languageKey = stringPreferencesKey("language")
    private val apiKeyKey = stringPreferencesKey("api_key")
    private val aiDailyLimitKey = intPreferencesKey("ai_daily_limit")
    private val aiModelKey = stringPreferencesKey("ai_model")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val notifyOnTaskDueKey = booleanPreferencesKey("notify_on_task_due")
    private val notifyOnAiReplyKey = booleanPreferencesKey("notify_on_ai_reply")

    fun observeSettings(): Flow<SettingsState> = context.settingsDataStore.data.map { prefs ->
        SettingsState(
            themeMode = prefs[themeKey]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            language = prefs[languageKey]?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
                ?: AppLanguage.SYSTEM,
            apiKey = prefs[apiKeyKey] ?: "",
            aiDailyLimit = prefs[aiDailyLimitKey] ?: 50,
            aiModel = prefs[aiModelKey] ?: "gpt-4o-mini",
            notificationsEnabled = prefs[notificationsEnabledKey] ?: true,
            notifyOnTaskDue = prefs[notifyOnTaskDueKey] ?: true,
            notifyOnAiReply = prefs[notifyOnAiReplyKey] ?: true
        )
    }

    suspend fun update(block: (MutableMap<String, Any>) -> Unit) {
        val updates = mutableMapOf<String, Any>()
        block(updates)
        context.settingsDataStore.edit { prefs ->
            updates.forEach { (key, value) ->
                when (key) {
                    "theme_mode" -> prefs[themeKey] = value as String
                    "language" -> prefs[languageKey] = value as String
                    "api_key" -> prefs[apiKeyKey] = value as String
                    "ai_daily_limit" -> prefs[aiDailyLimitKey] = value as Int
                    "ai_model" -> prefs[aiModelKey] = value as String
                    "notifications_enabled" -> prefs[notificationsEnabledKey] = value as Boolean
                    "notify_on_task_due" -> prefs[notifyOnTaskDueKey] = value as Boolean
                    "notify_on_ai_reply" -> prefs[notifyOnAiReplyKey] = value as Boolean
                }
            }
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[themeKey] = mode.name }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.settingsDataStore.edit { it[languageKey] = lang.name }
    }

    suspend fun setApiKey(key: String) {
        context.settingsDataStore.edit { it[apiKeyKey] = key }
    }

    suspend fun setAiDailyLimit(limit: Int) {
        context.settingsDataStore.edit { it[aiDailyLimitKey] = limit }
    }

    suspend fun setAiModel(model: String) {
        context.settingsDataStore.edit { it[aiModelKey] = model }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[notificationsEnabledKey] = enabled }
    }

    suspend fun setNotifyOnTaskDue(enabled: Boolean) {
        context.settingsDataStore.edit { it[notifyOnTaskDueKey] = enabled }
    }

    suspend fun setNotifyOnAiReply(enabled: Boolean) {
        context.settingsDataStore.edit { it[notifyOnAiReplyKey] = enabled }
    }
}
