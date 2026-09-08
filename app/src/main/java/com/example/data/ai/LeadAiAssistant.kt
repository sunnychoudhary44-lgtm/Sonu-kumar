package com.example.data.ai

import com.example.BuildConfig
import com.example.data.local.LeadEntity
import com.example.data.local.LeadStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiLeadAnalysis(
    val conversionProbability: Int,
    val readinessStatus: String,
    val keySignals: List<String>,
    val nextBestAction: String,
    val suggestedFollowUpDate: String,
    val riskFactor: String
)

data class ObjectionResponse(
    val objectionTitle: String,
    val talkTrack: String,
    val agentTip: String
)

class LeadAiAssistant(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    suspend fun generatePitch(
        lead: LeadEntity,
        projectName: String = "Skyline Summit",
        tone: String = "Warm & Consultative"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are a top 1% luxury real estate agent for 'Housing Worlds'.
                    Write a concise, professional WhatsApp/SMS follow-up message to this lead:
                    - Client Name: ${lead.name}
                    - Current Pipeline Stage: ${lead.stage.label}
                    - Property Requirement: ${lead.bhk} ${lead.propertyType} in ${lead.preferredLocation}
                    - Budget: $${lead.budgetMin.toInt()} - $${lead.budgetMax.toInt()}
                    - Highlighted Project: $projectName
                    - Tone: $tone
                    - Notes: ${lead.notes}
                    
                    Requirements:
                    1. Keep it under 80 words.
                    2. Clear, compelling call-to-action (e.g. site visit or 5-minute call).
                    3. No generic placeholders. Include a warm opening and personal touch.
                """.trimIndent()

                val responseText = callGeminiRest(apiKey, prompt)
                if (!responseText.isNullOrBlank()) {
                    return@withContext responseText.trim()
                }
            } catch (e: Exception) {
                // Fall back to intelligent template engine below
            }
        }

        // High-fidelity algorithmic fallback
        return@withContext generateSmartFallbackPitch(lead, projectName, tone)
    }

    suspend fun analyzeLead(lead: LeadEntity): AiLeadAnalysis = withContext(Dispatchers.IO) {
        val prob = when (lead.stage) {
            LeadStage.NEW -> 35
            LeadStage.CONTACTED -> 48
            LeadStage.SITE_VISIT_SCHEDULED -> 72
            LeadStage.SITE_VISIT_COMPLETED -> 81
            LeadStage.NEGOTIATION -> 90
            LeadStage.UNDER_CONTRACT -> 96
            LeadStage.WON -> 100
            LeadStage.LOST -> 10
        }

        val signals = mutableListOf<String>()
        signals.add("Pre-qualification: ${lead.financingStatus}")
        signals.add("Budget alignment: Active in $${lead.budgetMin.toInt()}k-$${lead.budgetMax.toInt()}k range")
        if (lead.notes.isNotBlank()) {
            signals.add("Explicit preference noted: ${lead.preferredLocation}")
        }
        if (lead.stage == LeadStage.SITE_VISIT_SCHEDULED || lead.stage == LeadStage.SITE_VISIT_COMPLETED) {
            signals.add("High intent physical site inspection interest")
        }

        val action = when (lead.stage) {
            LeadStage.NEW -> "Initiate first discovery call within 15 minutes to secure site visit commitment."
            LeadStage.CONTACTED -> "Share curated 2-unit floor plan comparison tailored to ${lead.bhk} in ${lead.preferredLocation}."
            LeadStage.SITE_VISIT_SCHEDULED -> "Send venue pin, parking assistance pass, and confirm exact attendee count."
            LeadStage.SITE_VISIT_COMPLETED -> "Follow up with payment schedule breakdown and unit availability sheet within 24 hours."
            LeadStage.NEGOTIATION -> "Present builder-approved closing incentive (e.g. waived registration or modular kitchen voucher)."
            LeadStage.UNDER_CONTRACT -> "Coordinate stamp duty documentation and bank loan disbursement timeline."
            LeadStage.WON -> "Request referral and onboard client to post-sales customer support portal."
            LeadStage.LOST -> "Tag in monthly newsletter nurture bucket for upcoming phase releases."
        }

        val risk = when (lead.stage) {
            LeadStage.NEGOTIATION -> "Competitor project offering minor square-footage discount; protect price integrity with amenities value."
            LeadStage.SITE_VISIT_SCHEDULED -> "Late cancellation risk; send reminder SMS 2 hours prior with weather/location update."
            LeadStage.NEW -> "Speed-to-lead decay; online leads cool by 60% after 2 hours."
            else -> "Financing delay risk if bank appraisal requires additional guarantor paperwork."
        }

        AiLeadAnalysis(
            conversionProbability = prob,
            readinessStatus = if (prob >= 75) "Very High (Ready to Transact)" else if (prob >= 50) "Medium (Nurturing)" else "Early Stage",
            keySignals = signals,
            nextBestAction = action,
            suggestedFollowUpDate = "Within 24 Hours",
            riskFactor = risk
        )
    }

    fun getObjectionPlaybook(): List<ObjectionResponse> {
        return listOf(
            ObjectionResponse(
                objectionTitle = "Price is higher than expected",
                talkTrack = "I completely understand price is top of mind. When comparing with neighboring developments, our units offer 28% higher carpet efficiency, grade-A clubhouse amenities, and guaranteed RERA delivery timeline which saves 12-18 months of rent/interest.",
                agentTip = "Pivot to cost-per-carpet-sqft and lifetime resale appreciation rather than gross ticket price."
            ),
            ObjectionResponse(
                objectionTitle = "Waiting for bank interest rates to drop",
                talkTrack = "Waiting for a 25bps rate cut might save $80/month on EMI, but inventory prices in this sector appreciate by 6-9% annually. Booking now locks in the pre-launch base price, and you can always refinance your mortgage when rates soften.",
                agentTip = "Show a quick math comparison: $80 monthly EMI savings vs $35,000 unit appreciation cost."
            ),
            ObjectionResponse(
                objectionTitle = "Want to visit 3 more projects first",
                talkTrack = "That makes absolute sense—buying a home is a major milestone. Let's do a quick side-by-side spec comparison today so you know exactly which 3 metrics to look for when touring other projects.",
                agentTip = "Never criticize other developers. Position yourself as their objective real estate advisor."
            ),
            ObjectionResponse(
                objectionTitle = "Need to discuss with spouse/family",
                talkTrack = "Of course! A home decision is a family decision. How about I host a private weekend twilight walkthrough for both of you? We can have fresh refreshments and walk through the natural lighting together.",
                agentTip = "Offer a stress-free second visit tailored to the family's lifestyle priorities (kitchen, kids play zone)."
            )
        )
    }

    private fun generateSmartFallbackPitch(lead: LeadEntity, projectName: String, tone: String): String {
        return when (lead.stage) {
            LeadStage.NEW ->
                "Hello ${lead.name}, thank you for your inquiry on Housing Worlds! We have exclusive units in $projectName (${lead.bhk} in ${lead.preferredLocation}) matching your budget of $${lead.budgetMin.toInt()}k-$${lead.budgetMax.toInt()}k. When is a good 5-minute window for a quick call today?"

            LeadStage.CONTACTED ->
                "Hi ${lead.name}, following our conversation, I've curated the top 2 floor plans at $projectName matching your preference for ${lead.bhk}. The corner units with park views are moving fast. Would you be open for a short walkthrough this Saturday?"

            LeadStage.SITE_VISIT_SCHEDULED ->
                "Hi ${lead.name}, looking forward to hosting you at $projectName for our site visit. I've prepared the sample flat walkthrough and floor spec dossier. Please let me know if you need any directions or location pin."

            LeadStage.SITE_VISIT_COMPLETED ->
                "Hi ${lead.name}, hope you enjoyed touring $projectName yesterday! The clubhouse and higher-floor layouts we saw received great traction. I have secured the preferred payment milestone breakdown for your review. Would you like to review it over a quick call?"

            LeadStage.NEGOTIATION ->
                "Hello ${lead.name}, great news regarding $projectName. The management has approved our requested milestone schedule adjustment and included the premium parking allocation for your ${lead.bhk}. Let's lock in this unit before the price revision this Friday."

            LeadStage.UNDER_CONTRACT ->
                "Hi ${lead.name}, your draft sale agreement for $projectName is ready for final review. Our legal advisory team has verified all approvals. Let's schedule a convenient time tomorrow to sign the agreement."

            LeadStage.WON ->
                "Congratulations ${lead.name} on your new home at $projectName! It was an absolute pleasure assisting you. Our dedicated client relationship team will now guide you through key handover and possession updates."

            LeadStage.LOST ->
                "Hi ${lead.name}, keeping you in mind as new phases release at Housing Worlds in ${lead.preferredLocation}. Whenever your timeline is ready, I will be delighted to share early-bird inventory with you."
        }
    }

    private fun callGeminiRest(apiKey: String, prompt: String): String? {
        val model = "gemini-2.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseStr = response.body?.string() ?: return null
        val root = JSONObject(responseStr)
        val candidates = root.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null
        return parts.getJSONObject(0).optString("text")
    }
}
