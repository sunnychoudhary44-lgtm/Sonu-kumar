package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties ORDER BY isFeatured DESC, id ASC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
    fun getPropertyById(id: Long): Flow<PropertyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(properties: List<PropertyEntity>)

    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getCount(): Int
}
