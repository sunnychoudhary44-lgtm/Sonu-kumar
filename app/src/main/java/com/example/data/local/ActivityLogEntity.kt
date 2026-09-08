package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ActivityType(val label: String) {
    CALL("Phone Call"),
    WHATSAPP("WhatsApp Message"),
    SITE_VISIT("Site Visit"),
    MEETING("In-person Meeting"),
    NOTE("Internal Note"),
    STAGE_CHANGE("Stage Progression"),
    PROPOSAL("Brochure / Proposal Sent")
}

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val leadId: Long,
    val type: ActivityType,
    val summary: String,
    val outcome: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
