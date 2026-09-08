package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :id LIMIT 1")
    fun getLeadById(id: Long): Flow<LeadEntity?>

    @Query("SELECT * FROM leads WHERE stage = :stage ORDER BY createdAt DESC")
    fun getLeadsByStage(stage: LeadStage): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE nextFollowUpDate IS NOT NULL ORDER BY nextFollowUpDate ASC")
    fun getFollowUpLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leads: List<LeadEntity>)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM leads")
    suspend fun getCount(): Int
}
