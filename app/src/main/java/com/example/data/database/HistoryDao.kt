package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM system_history ORDER BY timestamp ASC")
    fun getHistoryFlow(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM system_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(entity: HistoryEntity): Long

    @Query("DELETE FROM system_history WHERE timestamp < :cutoffTime")
    suspend fun pruneOldLogs(cutoffTime: Long)

    @Query("DELETE FROM system_history")
    suspend fun clearAllHistory()
}
