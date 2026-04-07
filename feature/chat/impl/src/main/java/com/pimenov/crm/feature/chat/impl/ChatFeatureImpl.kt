package com.pimenov.crm.feature.chat.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.pimenov.crm.feature.chat.api.ChatFeatureApi
import com.pimenov.crm.feature.chat.api.ChatNavigationRoute
import com.pimenov.crm.feature.chat.impl.ui.ChatScreen

class ChatFeatureImpl : ChatFeatureApi {
    override val route: String = ChatNavigationRoute.ROOT
    override fun registerGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable(ChatNavigationRoute.ROOT) { ChatScreen() }
    }
}
