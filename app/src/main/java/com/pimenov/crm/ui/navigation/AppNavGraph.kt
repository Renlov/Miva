package com.pimenov.crm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.pimenov.crm.feature.chat.api.ChatFeatureApi
import com.pimenov.crm.feature.notes.api.NotesFeatureApi
import com.pimenov.crm.feature.profile.api.ProfileFeatureApi
import com.pimenov.crm.feature.settings.api.SettingsFeatureApi
import com.pimenov.crm.feature.tasks.api.TasksFeatureApi
import org.koin.compose.koinInject

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val notesFeature: NotesFeatureApi = koinInject()
    val tasksFeature: TasksFeatureApi = koinInject()
    val chatFeature: ChatFeatureApi = koinInject()
    val profileFeature: ProfileFeatureApi = koinInject()
    val settingsFeature: SettingsFeatureApi = koinInject()

    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route,
        modifier = modifier
    ) {
        notesFeature.registerGraph(this, navController)
        tasksFeature.registerGraph(this, navController)
        chatFeature.registerGraph(this, navController)
        profileFeature.registerGraph(this, navController)
        settingsFeature.registerGraph(this, navController)
    }
}
