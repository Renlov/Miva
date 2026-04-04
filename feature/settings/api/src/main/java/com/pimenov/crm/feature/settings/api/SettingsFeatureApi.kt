package com.pimenov.crm.feature.settings.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface SettingsFeatureApi {

    val route: String

    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    )
}
