package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.DemoAccounts
import com.example.data.auth.UserRole
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.ui.LeadUiState
import com.example.ui.components.LeadCard
import com.example.ui.components.LeadFilterChipRow
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealAccent

@Composable
fun PipelineScreen(
    uiState: LeadUiState,
    onSearchChange: (String) -> Unit,
    onStageFilterChange: (LeadStage?) -> Unit,
    onPriorityFilterChange: (LeadPriority?) -> Unit,
    onMasterAgentFilterChange: (String) -> Unit,
    onOpenAccountSwitcher: () -> Unit,
    onLeadClick: (LeadEntity) -> Unit,
    onLogActivityClick: (LeadEntity) -> Unit,
    onAdvanceStage: (LeadEntity) -> Unit,
    onAddLeadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMaster = uiState.currentUser?.role == UserRole.MASTER_ADMIN
    val currentAgentTag = uiState.currentUser?.agentTag ?: "Sunny C."

    // 1. Role-based scoping of leads:
    // Sub-Agent sees only their assigned leads.
    // Master Admin sees all leads, or filters by specific sub-agent if selected.
    val roleScopedLeads by remember(uiState.leads, uiState.currentUser, uiState.masterAgentFilter) {
        derivedStateOf {
            if (isMaster) {
                if (uiState.masterAgentFilter == "All") {
                    uiState.leads
                } else {
                    uiState.leads.filter { it.assignedAgent.equals(uiState.masterAgentFilter, ignoreCase = true) }
                }
            } else {
                uiState.leads.filter { it.assignedAgent.equals(currentAgentTag, ignoreCase = true) }
            }
        }
    }

    val filteredLeads by remember(
        roleScopedLeads,
        uiState.searchQuery,
        uiState.stageFilter,
        uiState.priorityFilter
    ) {
        derivedStateOf {
            roleScopedLeads.filter { lead ->
                val matchesSearch = uiState.searchQuery.isBlank() ||
                        lead.name.contains(uiState.searchQuery, ignoreCase = true) ||
                        lead.phone.contains(uiState.searchQuery, ignoreCase = true) ||
                        lead.preferredLocation.contains(uiState.searchQuery, ignoreCase = true) ||
                        lead.bhk.contains(uiState.searchQuery, ignoreCase = true) ||
                        lead.propertyType.contains(uiState.searchQuery, ignoreCase = true)

                val matchesStage = uiState.stageFilter == null || lead.stage == uiState.stageFilter
                val matchesPriority = uiState.priorityFilter == null || lead.priority == uiState.priorityFilter

                matchesSearch && matchesStage && matchesPriority
            }
        }
    }

    val totalPipelineVal by remember(roleScopedLeads) {
        derivedStateOf {
            val sum = roleScopedLeads
                .filter { it.stage != LeadStage.LOST }
                .sumOf { (it.budgetMin + it.budgetMax) / 2.0 }
            if (sum >= 1_000_000) {
                String.format("$%.1fM", sum / 1_000_000.0)
            } else {
                String.format("$%.0fK", sum / 1_000.0)
            }
        }
    }

    val activeLeadsCount = roleScopedLeads.count { it.stage != LeadStage.WON && it.stage != LeadStage.LOST }
    val siteVisitsCount = roleScopedLeads.count { it.stage == LeadStage.SITE_VISIT_SCHEDULED }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero CRM Header with Brand and Auth Role Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(NavyPrimary, NavyLight)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = "Housing Worlds Logo",
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "HOUSING WORLDS",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = if (isMaster) "Master Pipeline View" else "Agent Pipeline • $currentAgentTag",
                                    fontSize = 11.sp,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Interactive Role Switcher Pill
                        Surface(
                            shape = CircleShape,
                            color = if (isMaster) Color(0x33C89B3C) else Color(0x330E7490),
                            modifier = Modifier
                                .clickable { onOpenAccountSwitcher() }
                                .testTag("account_switcher_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isMaster) Icons.Default.AdminPanelSettings else Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = if (isMaster) GoldAccent else Color(0xFF67E8F9),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isMaster) "Master Admin" else currentAgentTag,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch Account",
                                    tint = Color(0xCCFFFFFF),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            label = if (isMaster) "Agency Pipeline" else "My Pipeline",
                            value = totalPipelineVal,
                            icon = Icons.Default.TrendingUp,
                            accentColor = GoldAccent,
                            modifier = Modifier.weight(1.2f)
                        )
                        MetricCard(
                            label = if (isMaster) "All Leads" else "My Leads",
                            value = "$activeLeadsCount",
                            accentColor = Color(0xFF60A5FA),
                            modifier = Modifier.weight(0.8f)
                        )
                        MetricCard(
                            label = "Site Visits",
                            value = "$siteVisitsCount",
                            accentColor = TealAccent,
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    // Master Admin Only: Sub-Agent Assignment Filter Row
                    if (isMaster) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "TEAM:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xCCFFFFFF),
                                letterSpacing = 0.5.sp
                            )

                            // "All Agents" Chip
                            FilterChip(
                                selected = uiState.masterAgentFilter == "All",
                                onClick = { onMasterAgentFilterChange("All") },
                                label = { Text("All Agents (${uiState.leads.size})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = NavyPrimary
                                )
                            )

                            // Individual Sub-agents
                            DemoAccounts.ALL_SUB_AGENTS.forEach { subAgent ->
                                val count = uiState.leads.count { it.assignedAgent.equals(subAgent.agentTag, true) }
                                FilterChip(
                                    selected = uiState.masterAgentFilter == subAgent.agentTag,
                                    onClick = { onMasterAgentFilterChange(subAgent.agentTag) },
                                    label = { Text("${subAgent.agentTag} ($count)", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent,
                                        selectedLabelColor = NavyPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input"),
                    placeholder = {
                        Text(
                            text = if (isMaster) "Search all clients, location, agent..." else "Search my clients, location...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Filter Chips Horizontal Row
            LeadFilterChipRow(
                allLeads = roleScopedLeads,
                selectedStage = uiState.stageFilter,
                onStageSelected = onStageFilterChange,
                selectedPriority = uiState.priorityFilter,
                onPrioritySelected = onPriorityFilterChange
            )

            // Lead List
            if (filteredLeads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No leads in this view",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isMaster) "Try selecting 'All Agents' or adjusting filters" else "No leads assigned to $currentAgentTag match filters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = filteredLeads, key = { it.id }) { lead ->
                        LeadCard(
                            lead = lead,
                            onClick = { onLeadClick(lead) },
                            onLogActivity = { onLogActivityClick(lead) },
                            onAdvanceStage = { onAdvanceStage(lead) }
                        )
                    }
                }
            }
        }

        // Add Lead FAB
        FloatingActionButton(
            onClick = onAddLeadClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 88.dp, end = 16.dp)
                .testTag("add_lead_fab"),
            containerColor = GoldAccent,
            contentColor = NavyPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Lead"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "New Lead",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x2AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xCCFFFFFF)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }
        }
    }
}
