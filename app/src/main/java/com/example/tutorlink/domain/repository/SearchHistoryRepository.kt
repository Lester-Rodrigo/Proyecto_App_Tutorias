package com.example.tutorlink.domain.repository

import com.example.tutorlink.domain.model.SearchHistoryItem
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeHistory(): Flow<List<SearchHistoryItem>>

    suspend fun saveSearch(query: String)

    suspend fun clearHistory()
}