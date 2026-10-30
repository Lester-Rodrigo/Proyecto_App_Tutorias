package com.example.app_tutorias.data.repository

import com.example.app_tutorias.data.mapper.toDomain
import com.example.app_tutorias.data.mapper.toDto
import com.example.app_tutorias.data.remote.firestore.UserRemoteDataSource
import com.example.app_tutorias.domain.model.UserProfile
import com.example.app_tutorias.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override fun observeCurrentUserProfile(): Flow<UserProfile?> =
        remoteDataSource.observeCurrentUser().map { user -> user?.toDomain() }

    override suspend fun updateProfile(
        profile: UserProfile
    ): Result<Unit> = runCatching { remoteDataSource.updateUser(profile.toDto()) }
}