package com.example.tutorlink.domain.repository

import com.example.tutorlink.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUserProfile(): Flow<UserProfile?>

    suspend fun updateProfile(
        profile: UserProfile
    ): Result<Unit>
}