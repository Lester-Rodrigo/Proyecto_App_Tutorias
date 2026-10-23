package com.example.app_tutorias.domain.model

data class AvailabilitySlot(
    val id: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
)

data class Tutor(
    val id: String,
    val name: String,
    val initials: String,
    val description: String,
    val subjectIds: List<String>,
    val pricePerHour: Int,
    val averageRating: Double,
    val ratingCount: Int,
    val availability: List<AvailabilitySlot>
)