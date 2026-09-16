package com.example.app_tutorias.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app_tutorias.ui.components.TutorLinkBottomBar
import com.example.app_tutorias.ui.components.TutorLinkTopBar
import com.example.app_tutorias.ui.screens.CalendarScreen
import com.example.app_tutorias.ui.screens.HomeScreen
import com.example.app_tutorias.ui.screens.LoginScreen
import com.example.app_tutorias.ui.screens.SearchScreen
import com.example.app_tutorias.ui.screens.SettingsScreen
import com.example.app_tutorias.ui.theme.TutorLinkBackground

@Composable
fun TutorLinkNavHost() {
    val navController = rememberNavController()
    val backStackEntry by
    navController.currentBackStackEntryAsState()
    val currentRoute =
        backStackEntry?.destination?.route
    val showAppBars =
        currentRoute != null &&
                currentRoute != Destination.Login.route

    Scaffold(
        containerColor = TutorLinkBackground,
        topBar = {
            if (showAppBars) {
                TutorLinkTopBar(
                    onProfileClick = {
                        navController.navigate(Destination.Settings.route) {
                            launchSingleTop = true
                        }
                    },
                    onNotificationsClick = {
                    }
                )
            }
        },
        bottomBar = {
            if (showAppBars) {
                TutorLinkBottomBar(
                    currentRoute = currentRoute,
                    onDestinationSelected = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(
                                navController.graph
                                    .findStartDestination()
                                    .id
                            ) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Destination.Home.route) {
                            popUpTo(Destination.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.Home.route) {
                HomeScreen()
            }
            composable(Destination.Search.route) {
                SearchScreen()
            }
            composable(Destination.Calendar.route) {
                CalendarScreen()
            }
            composable(Destination.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Destination.Login.route) {
                            popUpTo(Destination.Home.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}