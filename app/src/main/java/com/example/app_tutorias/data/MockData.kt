package com.example.app_tutorias.data

import java.time.LocalDate
import java.time.LocalTime

object MockData {
    val categories = listOf(
        SubjectCategory("math", "Matemáticas", "∑"),
        SubjectCategory("science", "Ciencias", "⚗"),
        SubjectCategory("languages", "Idiomas", "文"),
        SubjectCategory("programming", "Programación", "</>")
    )

    val tutors = listOf(
        Tutor(
            id = "ana-morales",
            name = "Ana Morales",
            initials = "AM",
            specialty = "Matemáticas y Cálculo",
            description = "Ingeniera y tutora con cinco años de experiencia. Explica cada tema con ejemplos prácticos y ejercicios guiados.",
            rating = 4.9,
            pricePerHour = 85,
            availability = "Hoy · 4:00 p. m.",
            categoryIds = listOf("math")
        ),
        Tutor(
            id = "diego-lopez",
            name = "Diego López",
            initials = "DL",
            specialty = "Programación y Algoritmos",
            description = "Desarrollador de software enfocado en fundamentos, Kotlin y resolución estructurada de problemas.",
            rating = 4.8,
            pricePerHour = 95,
            availability = "Mañana · 10:00 a. m.",
            categoryIds = listOf("programming", "math")
        ),
        Tutor(
            id = "sofia-herrera",
            name = "Sofía Herrera",
            initials = "SH",
            specialty = "Inglés académico",
            description = "Profesora de idiomas especializada en conversación, escritura académica y preparación para evaluaciones.",
            rating = 4.7,
            pricePerHour = 75,
            availability = "Hoy · 6:30 p. m.",
            categoryIds = listOf("languages")
        ),
        Tutor(
            id = "carlos-mendez",
            name = "Carlos Méndez",
            initials = "CM",
            specialty = "Física y Química",
            description = "Docente universitario que conecta la teoría científica con experimentos y situaciones cotidianas.",
            rating = 4.9,
            pricePerHour = 90,
            availability = "Jueves · 3:00 p. m.",
            categoryIds = listOf("science", "math")
        )
    )

    fun initialSessions(today: LocalDate = LocalDate.now()) = listOf(
        TutoringSession(
            id = "session-upcoming-1",
            tutorId = "ana-morales",
            tutorName = "Ana Morales",
            subject = "Repaso de cálculo diferencial",
            date = today.plusDays(2),
            time = LocalTime.of(16, 0),
            notes = "Preparar ejercicios para el examen parcial.",
            status = TutoringStatus.UPCOMING
        ),
        TutoringSession(
            id = "session-completed-1",
            tutorId = "sofia-herrera",
            tutorName = "Sofía Herrera",
            subject = "Conversación en inglés",
            date = today.minusDays(5),
            time = LocalTime.of(17, 30),
            notes = "Práctica de presentación oral.",
            status = TutoringStatus.COMPLETED
        )
    )
}
