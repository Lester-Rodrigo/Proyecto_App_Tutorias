package com.example.app_tutorias.domain.model

import java.time.Instant

data class FavoriteTutor(
    val tutorId: String,
    val tutorName: String,
    val addedAt: Instant
)