package com.example.app_tutorias.data.remote.firestore

import java.util.Collections.emptyList

data class AvailabilitySlotDto(
    val id: String = "",
    val dayOfWeek: Int = 1,
    val startTime: String = "",
    val endTime: String = ""
)

data class TutorDto(
    val id: String = "",
    val name: String = "",
    val initials: String = "",
    val description: String = "",
    val subjectIds: List<String> = emptyList(),
    val pricePerHour: Int = 0,
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0,
    val availability: List<AvailabilitySlotDto> = emptyList()
)