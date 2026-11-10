package com.example.app_tutorias.data.repository

import com.example.app_tutorias.data.mapper.toDomain
import com.example.app_tutorias.data.remote.firestore.FavoritesRemoteDataSource
import com.example.app_tutorias.domain.model.FavoriteTutor
import com.example.app_tutorias.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val remoteDataSource: FavoritesRemoteDataSource
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FavoriteTutor>> =
        remoteDataSource.observeFavorites().map { favorites ->
            favorites.map { it.toDomain() }
        }

    override suspend fun addFavorite(
        tutorId: String,
        tutorName: String
    ): Result<Unit> = runCatching {
        remoteDataSource.addFavorite(tutorId, tutorName)
    }

    override suspend fun removeFavorite(
        tutorId: String
    ): Result<Unit> = runCatching {
        remoteDataSource.removeFavorite(tutorId)
    }
}