package com.example.app_tutorias.data

import java.time.LocalDate
import java.time.LocalTime

data class SubjectCategory(
    val id: String,
    val name: String,
    val emoji: String
)

data class Tutor(
    val id: String,
    val name: String,
    val initials: String,
    val specialty: String,
    val description: String,
    val rating: Double,
    val pricePerHour: Int,
    val availability: String,
    val categoryIds: List<String>
)

enum class TutoringStatus {
    UPCOMING,
    COMPLETED,
    CANCELLED
}

data class TutoringSession(
    val id: String,
    val tutorId: String,
    val tutorName: String,
    val subject: String,
    val date: LocalDate,
    val time: LocalTime,
    val notes: String,
    val status: TutoringStatus
)
