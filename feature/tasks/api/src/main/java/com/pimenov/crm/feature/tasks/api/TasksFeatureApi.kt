package com.pimenov.crm.feature.tasks.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface TasksFeatureApi {
    val route: String
    fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}
