package com.pimenov.crm.feature.settings.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.pimenov.crm.feature.settings.api.SettingsFeatureApi
import com.pimenov.crm.feature.settings.api.SettingsNavigationRoute
import com.pimenov.crm.feature.settings.impl.ui.SettingsScreen

class SettingsFeatureImpl : SettingsFeatureApi {

    override val route: String = SettingsNavigationRoute.ROOT

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.composable(route) {
            SettingsScreen()
        }
    }
}
