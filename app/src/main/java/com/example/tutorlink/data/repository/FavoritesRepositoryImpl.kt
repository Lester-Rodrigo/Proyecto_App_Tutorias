package com.example.tutorlink.data.repository

import com.example.tutorlink.data.mapper.toDomain
import com.example.tutorlink.data.remote.firestore.FavoritesRemoteDataSource
import com.example.tutorlink.domain.model.FavoriteTutor
import com.example.tutorlink.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.tutorlink.domain.reporting.ErrorReporter
import kotlinx.coroutines.CancellationException

class FavoritesRepositoryImpl(
    private val remoteDataSource: FavoritesRemoteDataSource,
    private val errorReporter: ErrorReporter
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
    }.onFailure { throwable ->
        if (throwable !is CancellationException) {
            errorReporter.recordNonFatal(
                throwable = throwable,
                operation = "favorites.add"
            )
        }
    }

    override suspend fun removeFavorite(
        tutorId: String
    ): Result<Unit> = runCatching {
        remoteDataSource.removeFavorite(tutorId)
    }.onFailure { throwable ->
        if (throwable !is CancellationException) {
            errorReporter.recordNonFatal(
                throwable = throwable,
                operation = "favorites.remove"
            )
        }
    }
}