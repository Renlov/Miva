package com.pimenov.crm.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.pimenov.crm.feature.settings.api.SettingsNavigationRoute
import com.pimenov.uikit.UiCoreString

sealed class Screen(val route: String, @StringRes val labelRes: Int, val icon: ImageVector) {
    data object Notes : Screen("notes", UiCoreString.nav_notes, Icons.AutoMirrored.Rounded.Notes)
    data object Chat : Screen("chat", UiCoreString.nav_chat, Icons.AutoMirrored.Rounded.Chat)
    data object Tasks : Screen("tasks", UiCoreString.nav_tasks, Icons.Rounded.Checklist)
    data object Settings : Screen(SettingsNavigationRoute.ROOT, UiCoreString.nav_settings, Icons.Rounded.Settings)
}

data object NoteEditor {
    const val ROUTE = "note_editor/{noteId}"
    fun createRoute(noteId: Long = -1L) = "note_editor/$noteId"
}

val bottomNavItems = listOf(Screen.Notes, Screen.Chat, Screen.Tasks, Screen.Settings)
