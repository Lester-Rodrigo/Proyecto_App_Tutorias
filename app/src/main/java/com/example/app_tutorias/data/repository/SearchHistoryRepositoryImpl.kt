package com.example.app_tutorias.data.repository

import com.example.app_tutorias.data.local.room.SearchHistoryDao
import com.example.app_tutorias.data.local.room.SearchHistoryEntity
import com.example.app_tutorias.data.remote.auth.AuthRemoteDataSource
import com.example.app_tutorias.domain.model.SearchHistoryItem
import com.example.app_tutorias.domain.repository.SearchHistoryRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val dao: SearchHistoryDao,
    private val authRemoteDataSource: AuthRemoteDataSource
) : SearchHistoryRepository {

    override fun observeHistory(): Flow<List<SearchHistoryItem>> =
        authRemoteDataSource.currentUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                dao.observeHistory(userId).map { history ->
                    history.map { entity ->
                        SearchHistoryItem(
                            id = entity.id,
                            userId = entity.userId,
                            query = entity.query,
                            searchedAt = Instant.ofEpochMilli(
                                entity.searchedAt
                            )
                        )
                    }
                }
            }
        }

    override suspend fun saveSearch(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return
        dao.insert(
            SearchHistoryEntity(
                userId = authRemoteDataSource
                    .requireCurrentUserId(),
                query = normalizedQuery,
                searchedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearHistory() {
        dao.clear(authRemoteDataSource.requireCurrentUserId())
    }
}