package com.example.app_tutorias.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: Flow<String?>

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signOut()
}