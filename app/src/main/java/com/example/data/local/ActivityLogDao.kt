package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs WHERE leadId = :leadId ORDER BY timestamp DESC")
    fun getLogsForLead(leadId: Long): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentActivities(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ActivityLogEntity>)

    @Query("DELETE FROM activity_logs WHERE leadId = :leadId")
    suspend fun deleteByLeadId(leadId: Long)
}
