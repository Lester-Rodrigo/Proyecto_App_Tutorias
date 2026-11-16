package com.example.tutorlink.domain.repository

import com.example.tutorlink.domain.model.FavoriteTutor
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteTutor>>

    suspend fun addFavorite(
        tutorId: String,
        tutorName: String
    ): Result<Unit>

    suspend fun removeFavorite(
        tutorId: String
    ): Result<Unit>
}