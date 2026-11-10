package com.example.app_tutorias.data.remote.firestore

import com.google.firebase.Timestamp

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "STUDENT",
    val emailNotificationsEnabled: Boolean = true,
    val createdAt: Timestamp = Timestamp(0, 0)
)