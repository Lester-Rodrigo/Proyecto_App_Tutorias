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
import com.example.app_tutorias.ui.TutorLinkViewModel
import com.example.app_tutorias.ui.components.TutorLinkBottomBar
import com.example.app_tutorias.ui.components.TutorLinkTopBar
import com.example.app_tutorias.ui.screens.CalendarScreen
import com.example.app_tutorias.ui.screens.HomeScreen
import com.example.app_tutorias.ui.screens.LoginScreen
import com.example.app_tutorias.ui.screens.SearchScreen
import com.example.app_tutorias.ui.screens.SettingsScreen
import com.example.app_tutorias.ui.screens.TutorDetailScreen
import com.example.app_tutorias.di.AppContainer
import com.example.app_tutorias.ui.login.AuthViewModel
import com.example.app_tutorias.ui.login.AuthViewModelFactory
import androidx.navigation.navDeepLink
import androidx.compose.material3.MaterialTheme
import com.example.app_tutorias.ui.TutorLinkViewModelFactory

@Composable
fun TutorLinkNavHost(appContainer: AppContainer, darkModeEnabled: Boolean, onDarkModeChanged: (Boolean) -> Unit){
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(appContainer.authRepository))
    val authUiState by
    authViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val viewModel: TutorLinkViewModel = viewModel(
        factory = TutorLinkViewModelFactory(
            catalogRepository = appContainer.catalogRepository,
            sessionRepository = appContainer.sessionRepository,
            favoritesRepository = appContainer.favoritesRepository,
            searchHistoryRepository = appContainer.searchHistoryRepository
        )
    )
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val calendarUiState by viewModel.calendarUiState.collectAsStateWithLifecycle()
    val bookingUiState by viewModel.bookingUiState.collectAsStateWithLifecycle()
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
        containerColor = MaterialTheme.colorScheme.background,
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
                    uiState = authUiState,
                    onNameChanged = authViewModel::onNameChanged,
                    onEmailChanged = authViewModel::onEmailChanged,
                    onPasswordChanged = authViewModel::onPasswordChanged,
                    onSubmit = authViewModel::submit,
                    onToggleMode = authViewModel::toggleMode,
                    onLoginSuccess = {
                        navController.navigate(
                            Destination.Home.route
                        ) {
                            popUpTo(Destination.Login.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Destination.Home.route) {
                HomeScreen(
                    categories = homeUiState.categories,
                    recommendedTutors = homeUiState.recommendedTutors,
                    nextSession = homeUiState.nextSession,
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
                    uiState = searchUiState,
                    onQueryChanged = viewModel::onSearchQueryChanged,
                    onClearQuery = viewModel::clearSearchQuery,
                    onCategorySelected = viewModel::onSearchCategorySelected,
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
                CalendarScreen(
                    uiState = calendarUiState,
                    onFilterSelected = viewModel::onSessionFilterSelected
                )
            }
            composable(
                route = Destination.TutorDetail.route,
                arguments = listOf(navArgument("tutorId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "tutorlink://tutor/{tutorId}" }
                )
            ) { entry ->
                val tutorId = entry.arguments?.getString("tutorId")
                val tutor = tutorId?.let(viewModel::tutorById)

                if (tutor != null) {
                    TutorDetailScreen(
                        tutor = tutor,
                        uiState = bookingUiState,
                        onBookingStarted = {
                            viewModel.beginBooking(tutor.id, entry.id)
                        },
                        onTopicChanged = viewModel::onBookingTopicChanged,
                        onDateChanged = viewModel::onBookingDateChanged,
                        onTimeChanged = viewModel::onBookingTimeChanged,
                        onNotesChanged = viewModel::onBookingNotesChanged,
                        onSubmit = { viewModel.submitBooking(tutor) },
                        onBack = { navController.popBackStack() },
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
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeChanged = onDarkModeChanged,
                    onLogout = {
                        authViewModel.signOut()
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
