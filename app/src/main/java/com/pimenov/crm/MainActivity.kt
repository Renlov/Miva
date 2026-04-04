package com.pimenov.crm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pimenov.crm.feature.settings.impl.data.SettingsPreferences
import com.pimenov.crm.feature.settings.impl.data.ThemeMode
import com.pimenov.crm.ui.navigation.AppNavGraph
import com.pimenov.crm.ui.navigation.BottomNavBar
import com.pimenov.crm.ui.navigation.Screen
import com.pimenov.crm.ui.theme.CrmTheme
import com.pimenov.crm.widget.OpenTasksAction
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs: SettingsPreferences = koinInject()
            val settings by prefs.observeSettings().collectAsState(
                initial = com.pimenov.crm.feature.settings.impl.data.SettingsState()
            )
            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            CrmTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                val navigateTo = intent?.getStringExtra(OpenTasksAction.EXTRA_NAVIGATE_TO)
                if (navigateTo != null) {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        navController.navigate(Screen.Tasks.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        intent?.removeExtra(OpenTasksAction.EXTRA_NAVIGATE_TO)
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
