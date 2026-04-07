package com.pimenov.crm.feature.notes.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pimenov.crm.feature.notes.api.NotesFeatureApi
import com.pimenov.crm.feature.notes.api.NotesNavigationRoute
import com.pimenov.crm.feature.notes.impl.ui.NoteEditorScreen
import com.pimenov.crm.feature.notes.impl.ui.NotesListScreen

class NotesFeatureImpl : NotesFeatureApi {
    override val route: String = NotesNavigationRoute.ROOT

    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable(NotesNavigationRoute.ROOT) {
            NotesListScreen(
                onNoteClick = { noteId -> navController.navigate(NotesNavigationRoute.editorRoute(noteId)) },
                onNewNote = { navController.navigate(NotesNavigationRoute.editorRoute()) }
            )
        }
        navGraphBuilder.composable(
            route = NotesNavigationRoute.EDITOR,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteEditorScreen(noteId = noteId, onBack = { navController.popBackStack() })
        }
    }
}
