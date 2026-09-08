package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiLeadAnalysis
import com.example.data.ai.LeadAiAssistant
import com.example.data.ai.ObjectionResponse
import com.example.data.auth.DemoAccounts
import com.example.data.auth.UserAccount
import com.example.data.auth.UserRole
import com.example.data.local.ActivityLogEntity
import com.example.data.local.ActivityType
import com.example.data.local.AppDatabase
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.data.local.PropertyEntity
import com.example.data.repository.LeadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class LeadUiState(
    // Auth & Session
    val currentUser: UserAccount? = null,
    val masterAgentFilter: String = "All", // "All" or "Sunny C.", "Priya V.", "Rohan M."
    val isAccountSwitcherOpen: Boolean = false,

    // Data
    val leads: List<LeadEntity> = emptyList(),
    val properties: List<PropertyEntity> = emptyList(),
    val recentActivities: List<ActivityLogEntity> = emptyList(),
    val followUpLeads: List<LeadEntity> = emptyList(),

    // Navigation & Filters
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val stageFilter: LeadStage? = null,
    val priorityFilter: LeadPriority? = null,

    // Selected Lead Detail
    val selectedLead: LeadEntity? = null,
    val selectedLeadLogs: List<ActivityLogEntity> = emptyList(),
    val selectedLeadMatches: List<PropertyEntity> = emptyList(),

    // Dialogs
    val isAddEditDialogOpen: Boolean = false,
    val leadBeingEdited: LeadEntity? = null,
    val isLogActivityDialogOpen: Boolean = false,
    val leadForActivity: LeadEntity? = null,

    // AI Features
    val isAiAnalyzing: Boolean = false,
    val aiAnalysis: AiLeadAnalysis? = null,
    val isAiPitchGenerating: Boolean = false,
    val aiPitch: String? = null,
    val objectionPlaybook: List<ObjectionResponse> = emptyList(),
    val messageSnackbar: String? = null
)

class LeadViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = LeadRepository(
        database.leadDao(),
        database.activityLogDao(),
        database.propertyDao()
    )
    private val aiAssistant = LeadAiAssistant()

    private val _uiState = MutableStateFlow(
        LeadUiState(
            // Start logged in as Master Admin by default so preview is immediately functional,
            // with prominent one-tap switcher between Master Login and Sub-Agent Logins!
            currentUser = DemoAccounts.MASTER_ADMIN,
            objectionPlaybook = aiAssistant.getObjectionPlaybook()
        )
    )
    val uiState: StateFlow<LeadUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        viewModelScope.launch {
            combine(
                repository.allLeads,
                repository.allProperties,
                repository.recentActivities,
                repository.followUpLeads
            ) { leads, properties, activities, followUps ->
                _uiState.value.copy(
                    leads = leads,
                    properties = properties,
                    recentActivities = activities,
                    followUpLeads = followUps
                )
            }.collect { newState ->
                _uiState.value = newState
                val currentSelected = _uiState.value.selectedLead
                if (currentSelected != null) {
                    val updated = newState.leads.find { it.id == currentSelected.id }
                    if (updated != null) {
                        selectLead(updated)
                    }
                }
            }
        }
    }

    // --- Authentication & Multi-tier Login ---

    fun login(email: String, passcode: String, asMaster: Boolean): Boolean {
        val user = DemoAccounts.authenticate(email, passcode, asMaster)
        return if (user != null) {
            _uiState.value = _uiState.value.copy(
                currentUser = user,
                masterAgentFilter = "All"
            )
            showSnackbar("Welcome, ${user.name} (${user.role.label})")
            true
        } else {
            showSnackbar("Invalid credentials for ${if (asMaster) "Master Admin" else "Sub-Agent"}")
            false
        }
    }

    fun quickLogin(user: UserAccount) {
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            masterAgentFilter = "All",
            isAccountSwitcherOpen = false
        )
        showSnackbar("Switched to ${user.role.label}: ${user.name}")
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            currentUser = null,
            isAccountSwitcherOpen = false,
            selectedLead = null
        )
        showSnackbar("Logged out successfully")
    }

    fun setMasterAgentFilter(agentTag: String) {
        _uiState.value = _uiState.value.copy(masterAgentFilter = agentTag)
    }

    fun toggleAccountSwitcher(open: Boolean) {
        _uiState.value = _uiState.value.copy(isAccountSwitcherOpen = open)
    }

    fun reassignLead(lead: LeadEntity, newAgentTag: String) {
        viewModelScope.launch {
            val updated = lead.copy(assignedAgent = newAgentTag)
            repository.saveLead(updated)

            // Log activity about reassignment
            repository.logActivity(
                ActivityLogEntity(
                    leadId = lead.id,
                    type = ActivityType.STAGE_CHANGE,
                    summary = "Lead reassigned from ${lead.assignedAgent} to $newAgentTag by Master Admin",
                    timestamp = System.currentTimeMillis()
                )
            )

            selectLead(updated)
            showSnackbar("Lead reassigned to $newAgentTag")
        }
    }

    // --- Navigation & Filtering ---

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setStageFilter(stage: LeadStage?) {
        _uiState.value = _uiState.value.copy(stageFilter = stage)
    }

    fun setPriorityFilter(priority: LeadPriority?) {
        _uiState.value = _uiState.value.copy(priorityFilter = priority)
    }

    fun selectLead(lead: LeadEntity?) {
        if (lead == null) {
            _uiState.value = _uiState.value.copy(
                selectedLead = null,
                selectedLeadLogs = emptyList(),
                selectedLeadMatches = emptyList(),
                aiAnalysis = null,
                aiPitch = null
            )
            return
        }

        val allProps = _uiState.value.properties
        val matchedProps = allProps.filter { prop ->
            val typeMatch = prop.propertyType.equals(lead.propertyType, ignoreCase = true)
            val bhkMatch = prop.bhk.equals(lead.bhk, ignoreCase = true)
            val budgetMatch = prop.price >= lead.budgetMin * 0.8 && prop.price <= lead.budgetMax * 1.2
            (typeMatch || bhkMatch) && budgetMatch
        }.ifEmpty {
            allProps.take(2)
        }

        _uiState.value = _uiState.value.copy(
            selectedLead = lead,
            selectedLeadMatches = matchedProps,
            aiAnalysis = null,
            aiPitch = null
        )

        viewModelScope.launch {
            repository.getLogsForLead(lead.id).collect { logs ->
                _uiState.value = _uiState.value.copy(selectedLeadLogs = logs)
            }
        }
    }

    fun openAddLeadDialog() {
        _uiState.value = _uiState.value.copy(
            isAddEditDialogOpen = true,
            leadBeingEdited = null
        )
    }

    fun openEditLeadDialog(lead: LeadEntity) {
        _uiState.value = _uiState.value.copy(
            isAddEditDialogOpen = true,
            leadBeingEdited = lead
        )
    }

    fun closeAddEditDialog() {
        _uiState.value = _uiState.value.copy(
            isAddEditDialogOpen = false,
            leadBeingEdited = null
        )
    }

    fun saveLead(lead: LeadEntity) {
        viewModelScope.launch {
            // If logged in as Sub-Agent, guarantee assignedAgent is the current agent
            val current = _uiState.value.currentUser
            val finalLead = if (current != null && current.role == UserRole.SUB_AGENT) {
                lead.copy(assignedAgent = current.agentTag)
            } else {
                lead
            }

            repository.saveLead(finalLead)
            closeAddEditDialog()
            showSnackbar(if (lead.id == 0L) "New lead added to pipeline" else "Lead details updated")
        }
    }

    fun updateLeadStage(lead: LeadEntity, newStage: LeadStage) {
        viewModelScope.launch {
            repository.updateLeadStage(lead, newStage)
            showSnackbar("Moved ${lead.name} to ${newStage.label}")
        }
    }

    fun deleteLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.deleteLead(lead)
            if (_uiState.value.selectedLead?.id == lead.id) {
                selectLead(null)
            }
            showSnackbar("Lead removed from CRM")
        }
    }

    fun openLogActivityDialog(lead: LeadEntity) {
        _uiState.value = _uiState.value.copy(
            isLogActivityDialogOpen = true,
            leadForActivity = lead
        )
    }

    fun closeLogActivityDialog() {
        _uiState.value = _uiState.value.copy(
            isLogActivityDialogOpen = false,
            leadForActivity = null
        )
    }

    fun logActivity(
        leadId: Long,
        type: ActivityType,
        summary: String,
        outcome: String? = null,
        nextFollowUpDate: Long? = null,
        nextFollowUpNote: String? = null
    ) {
        viewModelScope.launch {
            val activity = ActivityLogEntity(
                leadId = leadId,
                type = type,
                summary = summary,
                outcome = outcome,
                timestamp = System.currentTimeMillis()
            )
            repository.logActivity(activity)

            val lead = _uiState.value.leads.find { it.id == leadId }
            if (lead != null && (nextFollowUpDate != null || nextFollowUpNote != null)) {
                repository.saveLead(
                    lead.copy(
                        lastContactedAt = System.currentTimeMillis(),
                        nextFollowUpDate = nextFollowUpDate ?: lead.nextFollowUpDate,
                        nextFollowUpNote = nextFollowUpNote ?: lead.nextFollowUpNote
                    )
                )
            }
            closeLogActivityDialog()
            showSnackbar("Activity logged successfully")
        }
    }

    fun generateAiAnalysis(lead: LeadEntity) {
        _uiState.value = _uiState.value.copy(isAiAnalyzing = true)
        viewModelScope.launch {
            val analysis = aiAssistant.analyzeLead(lead)
            _uiState.value = _uiState.value.copy(
                isAiAnalyzing = false,
                aiAnalysis = analysis
            )
        }
    }

    fun generateAiPitch(lead: LeadEntity, projectName: String = "Skyline Summit", tone: String = "Warm & Consultative") {
        _uiState.value = _uiState.value.copy(isAiPitchGenerating = true)
        viewModelScope.launch {
            val pitch = aiAssistant.generatePitch(lead, projectName, tone)
            _uiState.value = _uiState.value.copy(
                isAiPitchGenerating = false,
                aiPitch = pitch
            )
        }
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(messageSnackbar = message)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(messageSnackbar = null)
    }
}
