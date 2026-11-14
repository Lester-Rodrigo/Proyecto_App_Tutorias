package com.example.tutorlink.data.repository

import com.example.tutorlink.data.remote.auth.AuthRemoteDataSource
import com.example.tutorlink.data.remote.firestore.UserDto
import com.example.tutorlink.data.remote.firestore.UserRemoteDataSource
import com.example.tutorlink.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import com.example.tutorlink.domain.reporting.ErrorReporter
import kotlinx.coroutines.CancellationException

class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val errorReporter: ErrorReporter
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
    }.onFailure { throwable ->
        if (throwable !is CancellationException) {
            errorReporter.recordNonFatal(
                throwable = throwable,
                operation = "auth.register"
            )
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        authRemoteDataSource.signIn(
            email = email.trim(),
            password = password
        )
    }.onFailure { throwable ->
        if (throwable !is CancellationException) {
            errorReporter.recordNonFatal(
                throwable = throwable,
                operation = "auth.signIn"
            )
        }
    }

    override suspend fun signOut() { authRemoteDataSource.signOut() }
}