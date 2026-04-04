package com.pimenov.crm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pimenov.crm.feature.settings.api.SettingsFeatureApi
import com.pimenov.crm.ui.chat.ChatScreen
import com.pimenov.crm.ui.notes.NoteEditorScreen
import com.pimenov.crm.ui.notes.NotesListScreen
import com.pimenov.crm.ui.tasks.TasksScreen
import org.koin.compose.koinInject

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val settingsFeature: SettingsFeatureApi = koinInject()

    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route,
        modifier = modifier
    ) {
        composable(Screen.Notes.route) {
            NotesListScreen(
                onNoteClick = { noteId ->
                    navController.navigate(NoteEditor.createRoute(noteId))
                },
                onNewNote = {
                    navController.navigate(NoteEditor.createRoute())
                }
            )
        }

        composable(
            route = NoteEditor.ROUTE,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteEditorScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen()
        }

        composable(Screen.Tasks.route) {
            TasksScreen()
        }

        settingsFeature.registerGraph(this, navController)
    }
}
