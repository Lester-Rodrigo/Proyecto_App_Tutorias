package com.example.app_tutorias.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_tutorias.ui.screens.CalendarScreen
import com.example.app_tutorias.ui.screens.HomeScreen
import com.example.app_tutorias.ui.screens.LoginScreen
import com.example.app_tutorias.ui.screens.SearchScreen
import com.example.app_tutorias.ui.screens.SettingsScreen

@Composable
fun TutorLinkNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.Login.route
    ) {
        composable(route = Destination.Login.route) {
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
        composable(route = Destination.Home.route) {
            HomeScreen(onSearchClick = {
                    navController.navigate(Destination.Search.route)
                },
                onCalendarClick = {
                    navController.navigate(Destination.Calendar.route)
                },
                onSettingsClick = {
                    navController.navigate(Destination.Settings.route)
                }
            )
        }
        composable(route = Destination.Search.route) {
            SearchScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Destination.Calendar.route) {
            CalendarScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Destination.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
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