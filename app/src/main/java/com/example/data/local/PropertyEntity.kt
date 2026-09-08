package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val projectDeveloper: String,
    val location: String,
    val propertyType: String,
    val bhk: String,
    val price: Double, // in Thousands or Lakhs
    val areaSqFt: Int,
    val status: String, // "Available", "Under Offer", "Ready to Move", "Pre-Launch"
    val highlights: String,
    val description: String,
    val isFeatured: Boolean = false
)
