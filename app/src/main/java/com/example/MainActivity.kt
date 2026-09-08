package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.LeadStage
import com.example.ui.LeadViewModel
import com.example.ui.screens.AccountSwitcherDialog
import com.example.ui.screens.AddEditLeadDialog
import com.example.ui.screens.AiPlaybookScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.LeadDetailSheet
import com.example.ui.screens.LogActivityDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PipelineScreen
import com.example.ui.screens.PropertiesScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary

class MainActivity : ComponentActivity() {
    private val viewModel: LeadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LeadCrmApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LeadCrmApp(viewModel: LeadViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.messageSnackbar) {
        val msg = uiState.messageSnackbar
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // If not logged in, show the Master / Sub-Agent Login Portal
    if (uiState.currentUser == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoginScreen(
                onLogin = viewModel::login,
                onQuickLogin = viewModel::quickLogin
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("login_snackbar_host")
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    Triple(0, "Pipeline", Icons.Filled.Apartment to Icons.Outlined.Apartment),
                    Triple(1, "Agenda", Icons.Filled.Schedule to Icons.Outlined.Schedule),
                    Triple(2, "Projects", Icons.Filled.HomeWork to Icons.Outlined.HomeWork),
                    Triple(3, "Analytics", Icons.Filled.BarChart to Icons.Outlined.BarChart),
                    Triple(4, "AI Copilot", Icons.Filled.AutoAwesome to Icons.Outlined.AutoAwesome)
                )

                items.forEach { (index, title, iconPair) ->
                    val isSelected = uiState.selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) iconPair.first else iconPair.second,
                                contentDescription = title
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            selectedTextColor = NavyPrimary,
                            indicatorColor = GoldAccent.copy(alpha = 0.35f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                0 -> PipelineScreen(
                    uiState = uiState,
                    onSearchChange = viewModel::setSearchQuery,
                    onStageFilterChange = viewModel::setStageFilter,
                    onPriorityFilterChange = viewModel::setPriorityFilter,
                    onMasterAgentFilterChange = viewModel::setMasterAgentFilter,
                    onOpenAccountSwitcher = { viewModel.toggleAccountSwitcher(true) },
                    onLeadClick = viewModel::selectLead,
                    onLogActivityClick = viewModel::openLogActivityDialog,
                    onAdvanceStage = { lead ->
                        val nextStage = when (lead.stage) {
                            LeadStage.NEW -> LeadStage.CONTACTED
                            LeadStage.CONTACTED -> LeadStage.SITE_VISIT_SCHEDULED
                            LeadStage.SITE_VISIT_SCHEDULED -> LeadStage.SITE_VISIT_COMPLETED
                            LeadStage.SITE_VISIT_COMPLETED -> LeadStage.NEGOTIATION
                            LeadStage.NEGOTIATION -> LeadStage.UNDER_CONTRACT
                            LeadStage.UNDER_CONTRACT -> LeadStage.WON
                            else -> null
                        }
                        if (nextStage != null) {
                            viewModel.updateLeadStage(lead, nextStage)
                        }
                    },
                    onAddLeadClick = viewModel::openAddLeadDialog
                )

                1 -> ScheduleScreen(
                    uiState = uiState,
                    onLeadClick = viewModel::selectLead,
                    onLogActivityClick = viewModel::openLogActivityDialog,
                    onMarkCompleted = { lead ->
                        viewModel.logActivity(
                            leadId = lead.id,
                            type = com.example.data.local.ActivityType.NOTE,
                            summary = "Follow-up completed: ${lead.nextFollowUpNote ?: "General"}",
                            outcome = "Task marked done",
                            nextFollowUpDate = null,
                            nextFollowUpNote = ""
                        )
                    }
                )

                2 -> PropertiesScreen(
                    uiState = uiState,
                    onLeadClick = viewModel::selectLead
                )

                3 -> AnalyticsScreen(
                    uiState = uiState
                )

                4 -> AiPlaybookScreen(
                    uiState = uiState,
                    onGeneratePitch = viewModel::generateAiPitch
                )
            }
        }

        // Lead Details Sheet
        if (uiState.selectedLead != null) {
            LeadDetailSheet(
                lead = uiState.selectedLead!!,
                uiState = uiState,
                onDismiss = { viewModel.selectLead(null) },
                onUpdateStage = { lead, stage -> viewModel.updateLeadStage(lead, stage) },
                onEditLead = { lead -> viewModel.openEditLeadDialog(lead) },
                onDeleteLead = { lead -> viewModel.deleteLead(lead) },
                onReassignLead = { lead, newAgent -> viewModel.reassignLead(lead, newAgent) },
                onLogActivity = { lead -> viewModel.openLogActivityDialog(lead) },
                onAnalyzeLead = { lead -> viewModel.generateAiAnalysis(lead) }
            )
        }

        // Add / Edit Lead Dialog
        if (uiState.isAddEditDialogOpen) {
            AddEditLeadDialog(
                leadToEdit = uiState.leadBeingEdited,
                onDismiss = viewModel::closeAddEditDialog,
                onSave = viewModel::saveLead
            )
        }

        // Log Activity Dialog
        if (uiState.isLogActivityDialogOpen && uiState.leadForActivity != null) {
            LogActivityDialog(
                lead = uiState.leadForActivity!!,
                onDismiss = viewModel::closeLogActivityDialog,
                onLog = { type, summary, outcome, nextDate, nextNote ->
                    viewModel.logActivity(
                        leadId = uiState.leadForActivity!!.id,
                        type = type,
                        summary = summary,
                        outcome = outcome,
                        nextFollowUpDate = nextDate,
                        nextFollowUpNote = nextNote
                    )
                }
            )
        }

        // Account & Role Switcher Dialog
        if (uiState.isAccountSwitcherOpen) {
            AccountSwitcherDialog(
                currentUser = uiState.currentUser,
                onSwitchUser = viewModel::quickLogin,
                onLogout = viewModel::logout,
                onDismiss = { viewModel.toggleAccountSwitcher(false) }
            )
        }
    }
}
