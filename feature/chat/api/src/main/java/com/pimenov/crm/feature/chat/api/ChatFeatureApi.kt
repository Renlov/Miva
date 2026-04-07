package com.pimenov.crm.feature.chat.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface ChatFeatureApi {
    val route: String
    fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}
