package com.example.app_tutorias.domain.model

import java.time.Instant

enum class UserRole {
    STUDENT,
    TUTOR
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val emailNotificationsEnabled: Boolean,
    val createdAt: Instant
)
