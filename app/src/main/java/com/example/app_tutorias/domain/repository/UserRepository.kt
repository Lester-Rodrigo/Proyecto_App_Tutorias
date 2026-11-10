package com.example.app_tutorias.domain.repository

import com.example.app_tutorias.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUserProfile(): Flow<UserProfile?>

    suspend fun updateProfile(
        profile: UserProfile
    ): Result<Unit>
}