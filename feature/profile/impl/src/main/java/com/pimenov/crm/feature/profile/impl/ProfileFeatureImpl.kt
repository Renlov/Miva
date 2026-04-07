package com.pimenov.crm.feature.profile.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.pimenov.crm.feature.profile.api.ProfileFeatureApi
import com.pimenov.crm.feature.profile.api.ProfileNavigationRoute
import com.pimenov.crm.feature.profile.impl.ui.ProfileScreen

class ProfileFeatureImpl : ProfileFeatureApi {
    override val route: String = ProfileNavigationRoute.ROOT
    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable(ProfileNavigationRoute.ROOT) { ProfileScreen() }
    }
}
