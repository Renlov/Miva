package com.pimenov.crm.feature.tasks.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.pimenov.crm.feature.tasks.api.TasksFeatureApi
import com.pimenov.crm.feature.tasks.api.TasksNavigationRoute
import com.pimenov.crm.feature.tasks.impl.ui.TasksScreen

class TasksFeatureImpl : TasksFeatureApi {
    override val route: String = TasksNavigationRoute.ROOT
    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable(TasksNavigationRoute.ROOT) { TasksScreen() }
    }
}
