package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.LeadEntity
import com.example.data.local.LeadStage
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate700
import com.example.ui.theme.TealAccent

fun formatBudget(budgetMin: Double, budgetMax: Double): String {
    fun formatVal(num: Double): String {
        return if (num >= 1000) {
            "$${(num / 1000).toInt()}k"
        } else {
            "$${num.toInt()}"
        }
    }
    return "${formatVal(budgetMin)} – ${formatVal(budgetMax)}"
}

fun getInitials(name: String): String {
    val parts = name.trim().split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].firstOrNull() ?: 'L'}${parts[1].firstOrNull() ?: 'C'}".uppercase()
        parts.isNotEmpty() -> "${parts[0].firstOrNull() ?: 'L'}".uppercase()
        else -> "HW"
    }
}

fun launchDialer(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun launchMessage(context: Context, phone: String, clientName: String) {
    try {
        val cleanPhone = phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        val uri = Uri.parse("smsto:$cleanPhone")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", "Hi $clientName, following up from Housing Worlds regarding your property inquiry...")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open messenger: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LeadCard(
    lead: LeadEntity,
    onClick: () -> Unit,
    onLogActivity: () -> Unit,
    onAdvanceStage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("lead_card_${lead.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Avatar, Name & Stage Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NavyPrimary, GoldAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(lead.name),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = lead.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Source: ${lead.source} • ${lead.assignedAgent}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    StageBadge(stage = lead.stage)
                    Spacer(modifier = Modifier.height(4.dp))
                    PriorityBadge(priority = lead.priority)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specs & Location Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = NavyPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${lead.bhk} • ${lead.propertyType}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TealAccent
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = lead.preferredLocation,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Budget and financing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BUDGET RANGE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = formatBudget(lead.budgetMin, lead.budgetMax),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
                FinancingBadge(status = lead.financingStatus)
            }

            // Follow-up Note Banner if available
            if (!lead.nextFollowUpNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = lead.nextFollowUpNote,
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Call & Text Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = { launchDialer(context, lead.phone) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("call_button_${lead.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFDCFCE7),
                            contentColor = Color(0xFF15803D)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call ${lead.name}",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { launchMessage(context, lead.phone, lead.name) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("sms_button_${lead.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0xFFE0F2FE),
                            contentColor = Color(0xFF0369A1)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Message ${lead.name}",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onLogActivity,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("log_activity_${lead.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Log activity for ${lead.name}",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Stage advance quick chip
                if (lead.stage != LeadStage.WON && lead.stage != LeadStage.LOST) {
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
                        OutlinedButton(
                            onClick = onAdvanceStage,
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("advance_stage_${lead.id}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Next: ${nextStage.label}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
