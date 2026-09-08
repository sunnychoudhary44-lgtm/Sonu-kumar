package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.DemoAccounts
import com.example.data.auth.UserRole
import com.example.data.local.ActivityLogEntity
import com.example.data.local.ActivityType
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.data.local.PropertyEntity
import com.example.ui.LeadUiState
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StageBadge
import com.example.ui.components.formatBudget
import com.example.ui.components.getInitials
import com.example.ui.components.launchDialer
import com.example.ui.components.launchMessage
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate900
import com.example.ui.theme.TealAccent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun launchEmail(context: Context, email: String, clientName: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, "Housing Worlds - Property Recommendation")
            putExtra(Intent.EXTRA_TEXT, "Hello $clientName,\n\nThank you for reaching out to Housing Worlds...")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open email: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailSheet(
    lead: LeadEntity,
    uiState: LeadUiState,
    onDismiss: () -> Unit,
    onUpdateStage: (LeadEntity, LeadStage) -> Unit,
    onEditLead: (LeadEntity) -> Unit,
    onDeleteLead: (LeadEntity) -> Unit,
    onReassignLead: (LeadEntity, String) -> Unit,
    onLogActivity: (LeadEntity) -> Unit,
    onAnalyzeLead: (LeadEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isMaster = uiState.currentUser?.role == UserRole.MASTER_ADMIN

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Avatar, Name, Close & Delete
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NavyPrimary, GoldAccent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getInitials(lead.name),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = lead.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${lead.source} • Created ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(lead.createdAt))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { onEditLead(lead) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Lead", tint = NavyPrimary)
                        }
                        if (isMaster) {
                            IconButton(onClick = { onDeleteLead(lead) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Lead (Master Only)", tint = HotLeadRed)
                            }
                        }
                    }
                }
            }

            // Master Admin Lead Reassignment Row
            if (isMaster) {
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👑 REASSIGN LEAD (MASTER ADMIN)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Current: ${lead.assignedAgent}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DemoAccounts.ALL_SUB_AGENTS.forEach { agent ->
                                    val isSelected = lead.assignedAgent.equals(agent.agentTag, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (!isSelected) {
                                                onReassignLead(lead, agent.agentTag)
                                            }
                                        },
                                        label = { Text("Assign: ${agent.agentTag}", fontSize = 11.sp) },
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
            }

            // Interactive Stage Progress Row
            item {
                Column {
                    Text(
                        text = "PIPELINE STAGE PROGRESSION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LeadStage.values().forEach { stage ->
                            FilterChip(
                                selected = lead.stage == stage,
                                onClick = { onUpdateStage(lead, stage) },
                                label = { Text(stage.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = Slate900
                                )
                            )
                        }
                    }
                }
            }

            // Fast Communication Action Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = { launchDialer(context, lead.phone) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFDCFCE7),
                            contentColor = Color(0xFF15803D)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 12.sp)
                    }

                    ElevatedButton(
                        onClick = { launchMessage(context, lead.phone, lead.name) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFE0F2FE),
                            contentColor = Color(0xFF0369A1)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Text", fontSize = 12.sp)
                    }

                    ElevatedButton(
                        onClick = { launchEmail(context, lead.email, lead.name) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFF3E8FF),
                            contentColor = Color(0xFF7E22CE)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Email", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = { onLogActivity(lead) },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Act", fontSize = 12.sp)
                    }
                }
            }

            // Lead Profile Key Facts Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "BUYER PREFERENCES & PROFILE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow("Budget Range", formatBudget(lead.budgetMin, lead.budgetMax))
                        DetailRow("Property Requirement", "${lead.bhk} • ${lead.propertyType}")
                        DetailRow("Preferred Locality", lead.preferredLocation)
                        DetailRow("Financing Status", lead.financingStatus)
                        DetailRow("Phone", lead.phone)
                        DetailRow("Email", lead.email)
                        DetailRow("Assigned Agent", lead.assignedAgent)

                        if (lead.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Notes & Requirements:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lead.notes,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // AI Deal Insights
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFB45309))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Deal Probability & Guidance",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            }

                            if (uiState.aiAnalysis == null) {
                                ElevatedButton(
                                    onClick = { onAnalyzeLead(lead) },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = GoldAccent,
                                        contentColor = NavyPrimary
                                    ),
                                    enabled = !uiState.isAiAnalyzing
                                ) {
                                    if (uiState.isAiAnalyzing) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = NavyPrimary)
                                    } else {
                                        Text("Run AI Analysis", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        if (uiState.aiAnalysis != null) {
                            val analysis = uiState.aiAnalysis
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CLOSING PROBABILITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                    Text(
                                        "${analysis.conversionProbability}%",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFB45309)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFDE68A)
                                ) {
                                    Text(
                                        text = analysis.readinessStatus,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { analysis.conversionProbability / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = GoldAccent,
                                trackColor = Color(0xFFFDE68A)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("NEXT BEST ACTION:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            Text(
                                text = analysis.nextBestAction,
                                fontSize = 12.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("RISK FACTOR:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Text(
                                text = analysis.riskFactor,
                                fontSize = 11.sp,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }
                }
            }

            // Matched Properties Recommendation Section
            if (uiState.selectedLeadMatches.isNotEmpty()) {
                item {
                    Text(
                        text = "MATCHED PROPERTIES (${uiState.selectedLeadMatches.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                }

                items(items = uiState.selectedLeadMatches, key = { "match_prop_${it.id}" }) { prop ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prop.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${prop.bhk} • $${prop.price.toInt()} • ${prop.location}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Hi ${lead.name}, here is a curated property that matches your criteria:\n\n*${prop.title}*\nLocation: ${prop.location}\nPrice: $${prop.price.toInt()}\nSpecs: ${prop.bhk} (${prop.areaSqFt} sq ft)\nHighlights: ${prop.highlights}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Recommend Property"))
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Recommend", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Timeline of Activities
            item {
                Text(
                    text = "ACTIVITY TIMELINE (${uiState.selectedLeadLogs.size})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
            }

            if (uiState.selectedLeadLogs.isEmpty()) {
                item {
                    Text(
                        text = "No activities logged yet for this lead.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(items = uiState.selectedLeadLogs, key = { "lead_log_${it.id}" }) { log ->
                    val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(log.timestamp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = log.type.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NavyPrimary
                                )
                                Text(text = dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(text = log.summary, fontSize = 12.sp)
                            if (!log.outcome.isNullOrBlank()) {
                                Text(
                                    text = "Outcome: ${log.outcome}",
                                    fontSize = 11.sp,
                                    color = TealAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
