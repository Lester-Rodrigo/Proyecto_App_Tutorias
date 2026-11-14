package com.example.tutorlink.navigation

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
import com.example.tutorlink.ui.components.TutorLinkBottomBar
import com.example.tutorlink.ui.components.TutorLinkTopBar
import com.example.tutorlink.ui.screens.CalendarScreen
import com.example.tutorlink.ui.screens.HomeScreen
import com.example.tutorlink.ui.screens.LoginScreen
import com.example.tutorlink.ui.screens.SearchScreen
import com.example.tutorlink.ui.screens.SettingsScreen
import com.example.tutorlink.ui.screens.TutorDetailScreen
import com.example.tutorlink.di.AppContainer
import com.example.tutorlink.ui.login.AuthViewModel
import com.example.tutorlink.ui.login.AuthViewModelFactory
import androidx.navigation.navDeepLink
import androidx.compose.material3.MaterialTheme
import com.example.tutorlink.ui.ScreenViewModelFactory
import com.example.tutorlink.ui.home.HomeViewModel
import com.example.tutorlink.ui.search.SearchViewModel
import com.example.tutorlink.ui.calendar.CalendarViewModel
import com.example.tutorlink.ui.detail.TutorDetailViewModel
import com.example.tutorlink.ui.components.DataStatus
import com.example.tutorlink.ui.UiLoadState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column

@Composable
fun TutorLinkNavHost(appContainer: AppContainer, darkModeEnabled: Boolean, onDarkModeChanged: (Boolean) -> Unit){
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(appContainer.authRepository))
    val authUiState by
    authViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val screenFactory = ScreenViewModelFactory(
        repository = appContainer.tutorLinkRepository,
        sessionRepository = appContainer.sessionRepository,
        searchHistoryRepository = appContainer.searchHistoryRepository
    )
    // Keep the existing host scope so tabs and booking forms survive navigation and rotation.
    val homeViewModel: HomeViewModel = viewModel(factory = screenFactory)
    val searchViewModel: SearchViewModel = viewModel(factory = screenFactory)
    val calendarViewModel: CalendarViewModel = viewModel(factory = screenFactory)
    val detailViewModel: TutorDetailViewModel = viewModel(factory = screenFactory)
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val calendarUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val bookingUiState by detailViewModel.bookingUiState.collectAsStateWithLifecycle()
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
                    userName = homeUiState.userName,
                    categories = homeUiState.categories,
                    recommendedTutors = homeUiState.recommendedTutors,
                    nextSession = homeUiState.nextSession,
                    loadState = homeUiState.loadState,
                    errorMessage = homeUiState.errorMessage,
                    onRetry = homeViewModel::retryLoading,
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
                    onQueryChanged = searchViewModel::onSearchQueryChanged,
                    onClearQuery = searchViewModel::clearSearchQuery,
                    onCategorySelected = searchViewModel::onSearchCategorySelected,
                    onSearchSubmitted = searchViewModel::onSearchSubmitted,
                    onHistorySelected = searchViewModel::onSearchHistorySelected,
                    onClearHistory = searchViewModel::clearSearchHistory,
                    onRetry = searchViewModel::retryLoading,
                    onTutorClick = { tutor ->
                        searchViewModel.onSearchSubmitted()
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
                    onFilterSelected = calendarViewModel::onSessionFilterSelected,
                    onRetry = calendarViewModel::retryLoading
                )
            }
            composable(
                route = Destination.TutorDetail.route,
                arguments = listOf(navArgument("tutorId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "tutorlink://tutor/{tutorId}" }
                )
            ) { entry ->
                val tutorId = entry.arguments?.getString("tutorId")
                val tutor = detailUiState.tutors.firstOrNull { it.id == tutorId }

                if (tutor != null) {
                    TutorDetailScreen(
                        tutor = tutor,
                        uiState = bookingUiState,
                        onBookingStarted = {
                            detailViewModel.beginBooking(tutor.id, entry.id)
                        },
                        onTopicChanged = detailViewModel::onBookingTopicChanged,
                        onDateChanged = detailViewModel::onBookingDateChanged,
                        onTimeChanged = detailViewModel::onBookingTimeChanged,
                        onNotesChanged = detailViewModel::onBookingNotesChanged,
                        onSubmit = { detailViewModel.submitBooking(tutor) },
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
                } else {
                    Column {
                        DataStatus(
                            loadState = if (detailUiState.loadState == UiLoadState.LOADING) UiLoadState.LOADING else UiLoadState.ERROR,
                            errorMessage = if (detailUiState.loadState == UiLoadState.LOADING) null else detailUiState.errorMessage ?: "No se encontró el tutor. Revisa la conexión o vuelve a buscar.",
                            onRetry = detailViewModel::retryLoading
                        )
                        TextButton(onClick = { navController.popBackStack() }) { Text("Volver") }
                    }
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
