package com.pimenov.crm.feature.notes.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface NotesFeatureApi {
    val route: String
    fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}
