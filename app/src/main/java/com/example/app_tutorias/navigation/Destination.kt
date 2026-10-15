package com.example.app_tutorias.navigation

sealed class Destination(
    val route: String,
    val label: String
) {
    data object Login : Destination(
        route = "login",
        label = "Iniciar sesión"
    )

    data object Home : Destination(
        route = "home",
        label = "Inicio"
    )

    data object Search : Destination(
        route = "search",
        label = "Buscar"
    )

    data object Calendar : Destination(
        route = "calendar",
        label = "Calendario"
    )

    data object TutorDetail : Destination(
        route = "tutor/{tutorId}",
        label = "Detalle del tutor"
    ) {
        fun createRoute(tutorId: String): String = "tutor/$tutorId"
    }

    data object Settings : Destination(
        route = "settings",
        label = "Configuración"
    )
}
