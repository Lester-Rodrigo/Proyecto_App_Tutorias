package com.example.app_tutorias.domain.model

import java.time.Instant

data class SearchHistoryItem(
    val id: Long = 0,
    val userId: String,
    val query: String,
    val searchedAt: Instant
)