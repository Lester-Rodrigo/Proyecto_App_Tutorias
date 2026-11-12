package com.example.tutorlink.data.repository

import com.example.tutorlink.data.mapper.toDomain
import com.example.tutorlink.data.mapper.toDto
import com.example.tutorlink.data.remote.firestore.UserRemoteDataSource
import com.example.tutorlink.domain.model.UserProfile
import com.example.tutorlink.domain.repository.UserRepository
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