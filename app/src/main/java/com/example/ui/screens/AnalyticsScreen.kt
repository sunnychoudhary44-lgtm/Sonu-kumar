package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.ui.LeadUiState
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent
import com.example.ui.theme.WarmLeadOrange
import com.example.ui.theme.WonGreen

@Composable
fun AnalyticsScreen(
    uiState: LeadUiState,
    modifier: Modifier = Modifier
) {
    val totalLeads = uiState.leads.size
    val wonLeads = uiState.leads.count { it.stage == LeadStage.WON }
    val wonValue = uiState.leads.filter { it.stage == LeadStage.WON }.sumOf { (it.budgetMin + it.budgetMax) / 2.0 }
    val activePipelineValue = uiState.leads.filter { it.stage != LeadStage.WON && it.stage != LeadStage.LOST }
        .sumOf { (it.budgetMin + it.budgetMax) / 2.0 }

    val conversionRate = if (totalLeads > 0) ((wonLeads.toFloat() / totalLeads) * 100).toInt() else 0

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(NavyPrimary, NavyLight)))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "PERFORMANCE & CONVERSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CRM Analytics",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // KPI Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x2AFFFFFF)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("CLOSED WON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xCCFFFFFF))
                                Text(
                                    text = "$${(wonValue / 1000).toInt()}K",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WonGreen
                                )
                                Text("$wonLeads Deals Closed", fontSize = 10.sp, color = Color(0xAAFFFFFF))
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x2AFFFFFF)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ACTIVE PIPELINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xCCFFFFFF))
                                Text(
                                    text = "$${(activePipelineValue / 1000).toInt()}K",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                                Text("${uiState.leads.count { it.stage != LeadStage.WON && it.stage != LeadStage.LOST }} In Progress", fontSize = 10.sp, color = Color(0xAAFFFFFF))
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x2AFFFFFF)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("WIN RATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xCCFFFFFF))
                                Text(
                                    text = "$conversionRate%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF60A5FA)
                                )
                                Text("Benchmark 14%", fontSize = 10.sp, color = Color(0xAAFFFFFF))
                            }
                        }
                    }
                }
            }
        }

        // Section 1: Conversion Funnel by Stage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pipeline Funnel Progression",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Distribution of leads across sales velocity stages",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val stages = listOf(
                        LeadStage.NEW to Color(0xFF0284C7),
                        LeadStage.CONTACTED to Color(0xFF4F46E5),
                        LeadStage.SITE_VISIT_SCHEDULED to Color(0xFFD97706),
                        LeadStage.SITE_VISIT_COMPLETED to Color(0xFFB45309),
                        LeadStage.NEGOTIATION to Color(0xFFEA580C),
                        LeadStage.UNDER_CONTRACT to Color(0xFF9333EA),
                        LeadStage.WON to WonGreen
                    )

                    stages.forEach { (stage, color) ->
                        val count = uiState.leads.count { it.stage == stage }
                        val frac = if (totalLeads > 0) count.toFloat() / totalLeads else 0f

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stage.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$count leads (${(frac * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { frac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Lead Acquisition Source Share
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = TealAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lead Acquisition Sources",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val sources = listOf("Website Form", "MagicBricks", "Referral", "Meta Ad", "Direct Call", "Walk-in")
                    sources.forEach { source ->
                        val count = uiState.leads.count { it.source.equals(source, true) }
                        if (count > 0 || totalLeads <= 6) {
                            val pct = if (totalLeads > 0) (count * 100) / totalLeads else 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(NavyPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = source,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "$count ($pct%)",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Lead Priority Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Priority Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val hotCount = uiState.leads.count { it.priority == LeadPriority.HOT }
                        val warmCount = uiState.leads.count { it.priority == LeadPriority.WARM }
                        val coldCount = uiState.leads.count { it.priority == LeadPriority.COLD }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🔥 HOT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HotLeadRed)
                                Text("$hotCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HotLeadRed)
                                Text("High Intent", fontSize = 10.sp, color = Color(0xFF991B1B))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFEDD5),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("WARM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarmLeadOrange)
                                Text("$warmCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WarmLeadOrange)
                                Text("Nurturing", fontSize = 10.sp, color = Color(0xFF9A3412))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDBEAFE),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("COLD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                Text("$coldCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                Text("Long Term", fontSize = 10.sp, color = Color(0xFF1E40AF))
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Master Admin Only - Sub-Agent Team Leaderboard
        if (uiState.currentUser?.role == com.example.data.auth.UserRole.MASTER_ADMIN) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Agent Performance Leaderboard",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Master Admin view of team revenue and active volume",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        com.example.data.auth.DemoAccounts.ALL_SUB_AGENTS.forEach { subAgent ->
                            val agentLeads = uiState.leads.filter { it.assignedAgent.equals(subAgent.agentTag, true) }
                            val agentWon = agentLeads.filter { it.stage == LeadStage.WON }
                            val agentWonVal = agentWon.sumOf { (it.budgetMin + it.budgetMax) / 2.0 }
                            val agentActive = agentLeads.count { it.stage != LeadStage.WON && it.stage != LeadStage.LOST }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(TealAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = subAgent.agentTag.take(2),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = subAgent.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "$agentActive Active • ${agentWon.size} Won",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$${(agentWonVal / 1000).toInt()}k",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = WonGreen
                                        )
                                        Text(
                                            text = "Closed Volume",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
