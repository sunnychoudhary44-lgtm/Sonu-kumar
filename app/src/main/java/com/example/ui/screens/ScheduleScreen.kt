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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.UserRole
import com.example.data.local.ActivityType
import com.example.data.local.LeadEntity
import com.example.data.local.LeadStage
import com.example.ui.LeadUiState
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StageBadge
import com.example.ui.components.launchDialer
import com.example.ui.components.launchMessage
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleScreen(
    uiState: LeadUiState,
    onLeadClick: (LeadEntity) -> Unit,
    onLogActivityClick: (LeadEntity) -> Unit,
    onMarkCompleted: (LeadEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isMaster = uiState.currentUser?.role == UserRole.MASTER_ADMIN
    val currentAgentTag = uiState.currentUser?.agentTag ?: "Sunny C."
    val relevantLeads = if (isMaster) uiState.leads else uiState.leads.filter { it.assignedAgent.equals(currentAgentTag, true) }
    val followUpLeads = relevantLeads.filter { !it.nextFollowUpNote.isNullOrBlank() }
    val siteVisits = relevantLeads.filter { it.stage == LeadStage.SITE_VISIT_SCHEDULED }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(NavyPrimary, NavyLight)))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY AGENDA & FOLLOW-UPS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (isMaster) "Agency Action Center (All Agents)" else "My Action Center ($currentAgentTag)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0x33FFFFFF)
                        ) {
                            Text(
                                text = "${followUpLeads.size} Due Actions",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Speed-to-lead and regular touchpoints increase property deal conversion by 3.8x.",
                        fontSize = 12.sp,
                        color = Color(0xCCFFFFFF)
                    )
                }
            }
        }

        // Section: Upcoming Site Visits
        if (siteVisits.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Site Visits Scheduled (${siteVisits.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            items(items = siteVisits, key = { "visit_${it.id}" }) { lead ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lead.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF78350F)
                            )
                            PriorityBadge(priority = lead.priority)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Inspecting: ${lead.bhk} ${lead.propertyType} in ${lead.preferredLocation}",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                        if (!lead.nextFollowUpNote.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "📍 ${lead.nextFollowUpNote}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ElevatedButton(
                                onClick = { launchDialer(context, lead.phone) },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color(0xFF15803D),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Buyer", fontSize = 12.sp)
                            }
                            FilledTonalButton(
                                onClick = { onLogActivityClick(lead) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Visit", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: Active Follow-up Tasks
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Follow-up Queue (${followUpLeads.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (followUpLeads.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "All caught up!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "No pending follow-up reminders right now.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(items = followUpLeads, key = { "task_${it.id}" }) { lead ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lead.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${lead.bhk} • ${lead.preferredLocation} • $${lead.budgetMin.toInt()}k-$${lead.budgetMax.toInt()}k",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StageBadge(stage = lead.stage)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Task Box
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = NavyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lead.nextFollowUpNote ?: "Scheduled follow-up",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { launchDialer(context, lead.phone) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 12.sp)
                            }
                            FilledTonalButton(
                                onClick = { launchMessage(context, lead.phone, lead.name) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Text", fontSize = 12.sp)
                            }
                            ElevatedButton(
                                onClick = { onMarkCompleted(lead) },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color(0xFFDCFCE7),
                                    contentColor = Color(0xFF15803D)
                                ),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Complete", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section: Recent Activity Stream
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TealAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent CRM Activities",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(items = uiState.recentActivities.take(10), key = { "act_${it.id}" }) { act ->
            val lead = uiState.leads.find { it.id == act.leadId }
            val timeFmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(act.timestamp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when (act.type) {
                                    ActivityType.CALL -> Color(0xFFDCFCE7)
                                    ActivityType.SITE_VISIT -> Color(0xFFFEF3C7)
                                    ActivityType.WHATSAPP -> Color(0xFFE0F2FE)
                                    ActivityType.STAGE_CHANGE -> Color(0xFFF3E8FF)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (act.type) {
                                ActivityType.CALL -> "📞"
                                ActivityType.SITE_VISIT -> "🏡"
                                ActivityType.WHATSAPP -> "💬"
                                ActivityType.STAGE_CHANGE -> "⚡"
                                ActivityType.MEETING -> "🤝"
                                else -> "📝"
                            },
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lead?.name ?: "Client #${act.leadId}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = timeFmt,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = act.summary,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!act.outcome.isNullOrBlank()) {
                            Text(
                                text = "Outcome: ${act.outcome}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TealAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
