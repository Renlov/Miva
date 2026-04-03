package com.pimenov.crm.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Notes : Screen("notes", "Заметки", Icons.AutoMirrored.Rounded.Notes)
    data object Chat : Screen("chat", "AI Чат", Icons.AutoMirrored.Rounded.Chat)
    data object Tasks : Screen("tasks", "Задачи", Icons.Rounded.Checklist)
    data object Settings : Screen("settings", "Настройки", Icons.Rounded.Settings)
}

data object NoteEditor {
    const val ROUTE = "note_editor/{noteId}"
    fun createRoute(noteId: Long = -1L) = "note_editor/$noteId"
}

val bottomNavItems = listOf(Screen.Notes, Screen.Chat, Screen.Tasks, Screen.Settings)
