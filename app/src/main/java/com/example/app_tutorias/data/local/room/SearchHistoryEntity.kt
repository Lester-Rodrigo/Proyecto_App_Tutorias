package com.example.app_tutorias.data.local.room

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [
        Index(
            value = ["userId", "query"],
            unique = true
        )
    ]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val query: String,
    val searchedAt: Long
)