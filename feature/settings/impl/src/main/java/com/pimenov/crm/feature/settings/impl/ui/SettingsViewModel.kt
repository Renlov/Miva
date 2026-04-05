package com.pimenov.crm.feature.settings.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pimenov.crm.feature.settings.impl.data.AppLanguage
import com.pimenov.crm.feature.settings.impl.data.SettingsPreferences
import com.pimenov.crm.feature.settings.impl.data.SettingsState
import com.pimenov.crm.feature.settings.impl.data.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isSignedIn: Boolean = false,
    val displayName: String? = null,
    val email: String? = null,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null
)

class SettingsViewModel(
    private val prefs: SettingsPreferences,
    private val onSyncRequested: suspend () -> Unit
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val settings = prefs.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    private val _authState = MutableStateFlow(buildAuthState())
    val authState = _authState.asStateFlow()

    private fun buildAuthState(): AuthState {
        val user = auth.currentUser
        return AuthState(
            isSignedIn = user != null,
            displayName = user?.displayName,
            email = user?.email
        )
    }

    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isSyncing = true, syncMessage = null)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _authState.value = buildAuthState().copy(isSyncing = true)
                onSyncRequested()
                _authState.value = _authState.value.copy(isSyncing = false, syncMessage = "synced")
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isSyncing = false,
                    syncMessage = e.message
                )
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState()
    }

    fun clearSyncMessage() {
        _authState.value = _authState.value.copy(syncMessage = null)
    }

    fun syncNow() {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isSyncing = true, syncMessage = null)
            try {
                onSyncRequested()
                _authState.value = _authState.value.copy(isSyncing = false, syncMessage = "synced")
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isSyncing = false,
                    syncMessage = e.message
                )
            }
        }
    }

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
