package com.example.data.repository

import com.example.data.local.ActivityLogDao
import com.example.data.local.ActivityLogEntity
import com.example.data.local.ActivityType
import com.example.data.local.LeadDao
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.data.local.PropertyDao
import com.example.data.local.PropertyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LeadRepository(
    private val leadDao: LeadDao,
    private val activityLogDao: ActivityLogDao,
    private val propertyDao: PropertyDao
) {
    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()
    val allProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()
    val recentActivities: Flow<List<ActivityLogEntity>> = activityLogDao.getRecentActivities()
    val followUpLeads: Flow<List<LeadEntity>> = leadDao.getFollowUpLeads()

    fun getLeadById(id: Long): Flow<LeadEntity?> = leadDao.getLeadById(id)
    fun getLogsForLead(leadId: Long): Flow<List<ActivityLogEntity>> = activityLogDao.getLogsForLead(leadId)
    fun getPropertyById(id: Long): Flow<PropertyEntity?> = propertyDao.getPropertyById(id)

    suspend fun saveLead(lead: LeadEntity): Long {
        val isNew = lead.id == 0L
        val id = leadDao.insertLead(lead)
        if (isNew) {
            activityLogDao.insertLog(
                ActivityLogEntity(
                    leadId = id,
                    type = ActivityType.NOTE,
                    summary = "New lead captured from ${lead.source}",
                    outcome = "Initial priority set to ${lead.priority.label}"
                )
            )
        }
        return id
    }

    suspend fun updateLeadStage(lead: LeadEntity, newStage: LeadStage) {
        val oldStage = lead.stage
        if (oldStage != newStage) {
            val updated = lead.copy(stage = newStage, lastContactedAt = System.currentTimeMillis())
            leadDao.updateLead(updated)
            activityLogDao.insertLog(
                ActivityLogEntity(
                    leadId = lead.id,
                    type = ActivityType.STAGE_CHANGE,
                    summary = "Moved stage from ${oldStage.label} to ${newStage.label}",
                    outcome = if (newStage == LeadStage.WON) "Deal successfully closed!" else null
                )
            )
        }
    }

    suspend fun logActivity(log: ActivityLogEntity) {
        activityLogDao.insertLog(log)
    }

    suspend fun deleteLead(lead: LeadEntity) {
        activityLogDao.deleteByLeadId(lead.id)
        leadDao.deleteLead(lead)
    }

    suspend fun checkAndSeedInitialData() {
        if (leadDao.getCount() == 0) {
            seedProperties()
            seedLeads()
        }
    }

    private suspend fun seedProperties() {
        val properties = listOf(
            PropertyEntity(
                title = "Skyline Summit Residences",
                projectDeveloper = "Housing Worlds Premium Living",
                location = "Downtown Core",
                propertyType = "Apartment",
                bhk = "3 BHK",
                price = 490000.0,
                areaSqFt = 1680,
                status = "Few Units Left",
                highlights = "Infinity pool, Panoramic city skyline, Clubhouse, 2 EV parking slots",
                description = "Ultra-luxury modern 3-bedroom residences designed for high-performing professionals in Downtown Core.",
                isFeatured = true
            ),
            PropertyEntity(
                title = "Serene Palm Luxury Villas",
                projectDeveloper = "Housing Worlds Estates",
                location = "Palm District",
                propertyType = "Villa",
                bhk = "4+ BHK",
                price = 950000.0,
                areaSqFt = 3900,
                status = "Ready to Move",
                highlights = "Private plunge pool, Landscaped private terrace, Smart home automation",
                description = "Grand gated community villas with private gardens and Mediterranean architectural aesthetics.",
                isFeatured = true
            ),
            PropertyEntity(
                title = "The Grand Azure Penthouse",
                projectDeveloper = "Azure Coastal Heights",
                location = "Riverside Marina",
                propertyType = "Penthouse",
                bhk = "3 BHK",
                price = 720000.0,
                areaSqFt = 2800,
                status = "Available",
                highlights = "Panoramic waterfront views, Private elevator, Double-height ceilings",
                description = "Signature waterfront penthouse with floor-to-ceiling glass and private yacht marina access.",
                isFeatured = true
            ),
            PropertyEntity(
                title = "Green Valley Garden Homes",
                projectDeveloper = "EcoLiving Housing",
                location = "Green Valley Hills",
                propertyType = "Apartment",
                bhk = "2 BHK",
                price = 275000.0,
                areaSqFt = 1180,
                status = "Pre-Launch",
                highlights = "Zero-carbon footprint, 75% green cover, Near international school",
                description = "Modern and serene family-friendly apartments surrounded by lush greenery and jogging tracks.",
                isFeatured = false
            ),
            PropertyEntity(
                title = "Cyber Hub Executive Suites",
                projectDeveloper = "Nexus Commercial Realty",
                location = "Tech Corridor",
                propertyType = "Commercial",
                bhk = "Commercial",
                price = 580000.0,
                areaSqFt = 1350,
                status = "Available",
                highlights = "Grade A corporate tower, 24/7 HVAC, High rental yield 9.2%",
                description = "Prime commercial office space right at the heart of the bustling tech and startup corridor.",
                isFeatured = false
            )
        )
        propertyDao.insertAll(properties)
    }

    private suspend fun seedLeads() {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        val lead1 = LeadEntity(
            name = "Vikramaditya Sharma",
            phone = "+1 (555) 234-5678",
            email = "vikram.sharma@example.com",
            propertyType = "Apartment",
            bhk = "3 BHK",
            budgetMin = 450000.0,
            budgetMax = 580000.0,
            preferredLocation = "Downtown Core",
            stage = LeadStage.SITE_VISIT_SCHEDULED,
            priority = LeadPriority.HOT,
            source = "MagicBricks",
            financingStatus = "Pre-approved",
            assignedAgent = "Sunny C.",
            notes = "Very keen on Skyline Summit 3BHK higher floors (Tower B). Wants to inspect clubhouse and parking.",
            createdAt = now - (3 * oneDay),
            lastContactedAt = now - (4 * 3600000L),
            nextFollowUpDate = now + (2 * 3600000L), // today!
            nextFollowUpNote = "Host site visit at 3:00 PM at Skyline Summit"
        )
        val id1 = leadDao.insertLead(lead1)
        activityLogDao.insertLog(
            ActivityLogEntity(
                leadId = id1,
                type = ActivityType.CALL,
                summary = "Discussion on floor plans and Sunday site visit",
                outcome = "Confirmed 3:00 PM site inspection with spouse"
            )
        )
        activityLogDao.insertLog(
            ActivityLogEntity(
                leadId = id1,
                type = ActivityType.PROPOSAL,
                summary = "Shared Skyline Summit Tower B e-brochure via WhatsApp",
                outcome = "Client reviewed and liked 14th floor layout"
            )
        )

        val lead2 = LeadEntity(
            name = "Pooja & Rohan Malhotra",
            phone = "+1 (555) 876-5432",
            email = "rohan.malhotra@business.io",
            propertyType = "Villa",
            bhk = "4+ BHK",
            budgetMin = 850000.0,
            budgetMax = 1100000.0,
            preferredLocation = "Palm District",
            stage = LeadStage.NEGOTIATION,
            priority = LeadPriority.HOT,
            source = "Referral",
            financingStatus = "Cash Buyer",
            assignedAgent = "Sunny C.",
            notes = "Made initial offer for Serene Palm Villa #12. Negotiating on payment schedule and customized wooden deck.",
            createdAt = now - (8 * oneDay),
            lastContactedAt = now - (1 * oneDay),
            nextFollowUpDate = now + (5 * 3600000L),
            nextFollowUpNote = "Send revised payment milestone breakdown approved by builder"
        )
        val id2 = leadDao.insertLead(lead2)
        activityLogDao.insertLog(
            ActivityLogEntity(
                leadId = id2,
                type = ActivityType.MEETING,
                summary = "In-person negotiation meeting at sales gallery",
                outcome = "Agreed in principle, pending payment plan adjustment"
            )
        )

        val lead3 = LeadEntity(
            name = "David Chen",
            phone = "+1 (555) 432-1098",
            email = "david.chen@techmail.com",
            propertyType = "Apartment",
            bhk = "2 BHK",
            budgetMin = 260000.0,
            budgetMax = 320000.0,
            preferredLocation = "Green Valley Hills",
            stage = LeadStage.CONTACTED,
            priority = LeadPriority.WARM,
            source = "Website Form",
            financingStatus = "Needs Bank Loan",
            assignedAgent = "Sunny C.",
            notes = "Software engineer looking for peaceful suburban community. Inquired about HDFC / SBI home loan pre-qualification.",
            createdAt = now - (1 * oneDay),
            lastContactedAt = now - (12 * 3600000L),
            nextFollowUpDate = now + (24 * 3600000L),
            nextFollowUpNote = "Connect with preferred home loan advisor and send Green Valley price sheet"
        )
        val id3 = leadDao.insertLead(lead3)
        activityLogDao.insertLog(
            ActivityLogEntity(
                leadId = id3,
                type = ActivityType.CALL,
                summary = "Initial discovery call",
                outcome = "Expressed interest in Green Valley Garden 2BHK"
            )
        )

        val lead4 = LeadEntity(
            name = "Kavita Reddy",
            phone = "+1 (555) 345-6789",
            email = "kavita.reddy@corp.com",
            propertyType = "Penthouse",
            bhk = "3 BHK",
            budgetMin = 700000.0,
            budgetMax = 800000.0,
            preferredLocation = "Riverside Marina",
            stage = LeadStage.WON,
            priority = LeadPriority.HOT,
            source = "Walk-in",
            financingStatus = "Pre-approved",
            assignedAgent = "Sunny C.",
            notes = "Booked Grand Azure Penthouse #2102! Booking amount of 10% paid. Registration scheduled next week.",
            createdAt = now - (14 * oneDay),
            lastContactedAt = now - (2 * oneDay),
            nextFollowUpDate = null
        )
        val id4 = leadDao.insertLead(lead4)
        activityLogDao.insertLog(
            ActivityLogEntity(
                leadId = id4,
                type = ActivityType.STAGE_CHANGE,
                summary = "Moved to Closed Won",
                outcome = "Token amount received and unit blocked"
            )
        )

        val lead5 = LeadEntity(
            name = "Marcus Thorne",
            phone = "+1 (555) 901-2345",
            email = "marcus.thorne@designstudio.org",
            propertyType = "Apartment",
            bhk = "2 BHK",
            budgetMin = 250000.0,
            budgetMax = 300000.0,
            preferredLocation = "Green Valley Hills",
            stage = LeadStage.NEW,
            priority = LeadPriority.WARM,
            source = "Meta Ad",
            financingStatus = "Pre-approved",
            assignedAgent = "Sunny C.",
            notes = "Clicked on EcoLiving Green Valley Ad on Facebook. Inquired about balcony sizes and possession date.",
            createdAt = now - (3 * 3600000L),
            lastContactedAt = now - (3 * 3600000L),
            nextFollowUpDate = now + (1 * 3600000L),
            nextFollowUpNote = "First response call to verify requirement and share virtual tour"
        )
        val id5 = leadDao.insertLead(lead5)

        val lead6 = LeadEntity(
            name = "Ananya Sen",
            phone = "+1 (555) 678-9012",
            email = "ananya.sen@senassociates.in",
            propertyType = "Commercial",
            bhk = "Commercial",
            budgetMin = 500000.0,
            budgetMax = 650000.0,
            preferredLocation = "Tech Corridor",
            stage = LeadStage.UNDER_CONTRACT,
            priority = LeadPriority.HOT,
            source = "Direct Call",
            financingStatus = "Cash Buyer",
            assignedAgent = "Sunny C.",
            notes = "Legal diligence ongoing for Cyber Hub Executive Suite 404. Title search clear. Sale deed draft under review.",
            createdAt = now - (10 * oneDay),
            lastContactedAt = now - (1 * oneDay),
            nextFollowUpDate = now + (28 * 3600000L),
            nextFollowUpNote = "Receive signed draft agreement from legal team"
        )
        val id6 = leadDao.insertLead(lead6)
    }
}
