package com.example.app_tutorias.data.repository

import com.example.app_tutorias.data.remote.auth.AuthRemoteDataSource
import com.example.app_tutorias.data.remote.firestore.UserDto
import com.example.app_tutorias.data.remote.firestore.UserRemoteDataSource
import com.example.app_tutorias.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) : AuthRepository {
    override val currentUserId: Flow<String?> = authRemoteDataSource.currentUserId

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        val userId = authRemoteDataSource.register(
            email = email.trim(),
            password = password
        )
        userRemoteDataSource.saveUser(
            UserDto(
                id = userId,
                name = name.trim(),
                email = email.trim(),
                role = "STUDENT",
                emailNotificationsEnabled = true,
                createdAt = Timestamp.now()
            )
        )
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        authRemoteDataSource.signIn(
            email = email.trim(),
            password = password
        )
    }

    override suspend fun signOut() { authRemoteDataSource.signOut() }
}