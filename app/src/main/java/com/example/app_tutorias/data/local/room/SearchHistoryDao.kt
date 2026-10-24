package com.example.app_tutorias.data.local.room

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query(
        """
        SELECT * FROM search_history
        WHERE userId = :userId
        ORDER BY searchedAt DESC
        LIMIT 20
        """
    )
    fun observeHistory(userId: String): Flow<List<SearchHistoryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SearchHistoryEntity)
    @Query(
        """
        DELETE FROM search_history
        WHERE userId = :userId
        """
    )
    suspend fun clear(userId: String)
}