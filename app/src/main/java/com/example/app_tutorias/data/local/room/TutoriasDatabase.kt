package com.example.app_tutorias.data.local.room

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TutoriasDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}