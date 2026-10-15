package com.example.app_tutorias.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.ui.TutorLinkViewModel
import com.example.app_tutorias.ui.components.TutorLinkBottomBar
import com.example.app_tutorias.ui.components.TutorLinkTopBar
import com.example.app_tutorias.ui.screens.CalendarScreen
import com.example.app_tutorias.ui.screens.HomeScreen
import com.example.app_tutorias.ui.screens.LoginScreen
import com.example.app_tutorias.ui.screens.SearchScreen
import com.example.app_tutorias.ui.screens.SettingsScreen
import com.example.app_tutorias.ui.screens.TutorDetailScreen
import com.example.app_tutorias.ui.theme.TutorLinkBackground

@Composable
fun TutorLinkNavHost() {
    val navController = rememberNavController()
    val viewModel: TutorLinkViewModel = viewModel()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val backStackEntry by
    navController.currentBackStackEntryAsState()
    val currentRoute =
        backStackEntry?.destination?.route
    val appBarRoutes = setOf(
        Destination.Home.route,
        Destination.Search.route,
        Destination.Calendar.route,
        Destination.Settings.route
    )
    val showAppBars = currentRoute in appBarRoutes

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
                                Destination.Home.route
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
                val nextSession = sessions
                    .filter { it.status == TutoringStatus.UPCOMING }
                    .minWithOrNull(compareBy({ it.date }, { it.time }))
                HomeScreen(
                    categories = viewModel.categories,
                    recommendedTutors = viewModel.tutors.take(3),
                    nextSession = nextSession,
                    onSearchClick = {
                        navController.navigate(Destination.Search.route) {
                            launchSingleTop = true
                        }
                    },
                    onTutorClick = { tutor ->
                        navController.navigate(
                            Destination.TutorDetail.createRoute(tutor.id)
                        )
                    },
                    onCalendarClick = {
                        navController.navigate(Destination.Calendar.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.Search.route) {
                SearchScreen(
                    tutors = viewModel.tutors,
                    categories = viewModel.categories,
                    onTutorClick = { tutor ->
                        navController.navigate(
                            Destination.TutorDetail.createRoute(tutor.id)
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.Calendar.route) {
                CalendarScreen(sessions = sessions)
            }
            composable(
                route = Destination.TutorDetail.route,
                arguments = listOf(
                    navArgument("tutorId") { type = NavType.StringType }
                )
            ) { entry ->
                val tutorId = entry.arguments?.getString("tutorId")
                val tutor = tutorId?.let(viewModel::tutorById)

                if (tutor != null) {
                    TutorDetailScreen(
                        tutor = tutor,
                        onBack = { navController.popBackStack() },
                        onReservationConfirmed = viewModel::addReservation,
                        onViewCalendar = {
                            navController.navigate(Destination.Calendar.route) {
                                popUpTo(Destination.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
            composable(Destination.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Destination.Login.route) {
                            popUpTo(navController.graph.id) {
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
