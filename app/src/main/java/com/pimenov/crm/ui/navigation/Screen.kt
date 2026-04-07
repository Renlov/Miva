package com.pimenov.crm.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.pimenov.crm.feature.chat.api.ChatNavigationRoute
import com.pimenov.crm.feature.notes.api.NotesNavigationRoute
import com.pimenov.crm.feature.profile.api.ProfileNavigationRoute
import com.pimenov.crm.feature.settings.api.SettingsNavigationRoute
import com.pimenov.crm.feature.tasks.api.TasksNavigationRoute
import com.pimenov.uikit.UiCoreString

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val showLabel: Boolean = true
) {
    data object Notes : Screen(NotesNavigationRoute.ROOT, UiCoreString.nav_notes, Icons.AutoMirrored.Rounded.Notes)
    data object Tasks : Screen(TasksNavigationRoute.ROOT, UiCoreString.nav_tasks, Icons.Rounded.Checklist)
    data object Chat : Screen(ChatNavigationRoute.ROOT, UiCoreString.nav_chat, Icons.AutoMirrored.Rounded.Chat, showLabel = false)
    data object Profile : Screen(ProfileNavigationRoute.ROOT, UiCoreString.nav_profile, Icons.Rounded.Person)
    data object Settings : Screen(SettingsNavigationRoute.ROOT, UiCoreString.nav_settings, Icons.Rounded.Settings)
}

val bottomNavItems = listOf(Screen.Notes, Screen.Tasks, Screen.Chat, Screen.Profile, Screen.Settings)
