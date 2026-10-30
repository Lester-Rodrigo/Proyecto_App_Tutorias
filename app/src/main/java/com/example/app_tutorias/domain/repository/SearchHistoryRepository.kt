package com.example.app_tutorias.domain.repository

import com.example.app_tutorias.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeHistory(): Flow<List<SearchHistoryItem>>

    suspend fun saveSearch(query: String)

    suspend fun clearHistory()
}