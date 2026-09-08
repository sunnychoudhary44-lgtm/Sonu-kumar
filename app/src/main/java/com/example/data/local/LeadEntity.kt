package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LeadStage(val label: String, val stepIndex: Int) {
    NEW("New Lead", 0),
    CONTACTED("Contacted", 1),
    SITE_VISIT_SCHEDULED("Visit Scheduled", 2),
    SITE_VISIT_COMPLETED("Visit Done", 3),
    NEGOTIATION("Negotiation", 4),
    UNDER_CONTRACT("Under Contract", 5),
    WON("Closed Won", 6),
    LOST("Lost", 7)
}

enum class LeadPriority(val label: String) {
    HOT("Hot"),
    WARM("Warm"),
    COLD("Cold")
}

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val phone: String,
    val email: String,
    val propertyType: String, // e.g. "Apartment", "Villa", "Penthouse", "Commercial"
    val bhk: String, // e.g. "2 BHK", "3 BHK", "4+ BHK", "Studio"
    val budgetMin: Double, // in thousands or lakhs
    val budgetMax: Double,
    val preferredLocation: String,
    val stage: LeadStage = LeadStage.NEW,
    val priority: LeadPriority = LeadPriority.WARM,
    val source: String = "Website", // "Website", "MagicBricks", "Walk-in", "Referral", "Meta Ad"
    val financingStatus: String = "Pre-approved", // "Pre-approved", "Cash Buyer", "Needs Bank Loan"
    val assignedAgent: String = "Sunny C.",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastContactedAt: Long = System.currentTimeMillis(),
    val nextFollowUpDate: Long? = null,
    val nextFollowUpNote: String? = null
)
