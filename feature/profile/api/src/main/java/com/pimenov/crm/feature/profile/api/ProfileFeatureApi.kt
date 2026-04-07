package com.pimenov.crm.feature.profile.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface ProfileFeatureApi {
    val route: String
    fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}
