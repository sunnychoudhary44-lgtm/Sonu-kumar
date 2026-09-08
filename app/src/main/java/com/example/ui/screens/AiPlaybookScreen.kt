package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.local.LeadEntity
import com.example.ui.LeadUiState
import com.example.ui.components.launchMessage
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("AI Pitch", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

@Composable
fun AiPlaybookScreen(
    uiState: LeadUiState,
    onGeneratePitch: (LeadEntity, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLeadId by remember(uiState.leads) {
        mutableStateOf(uiState.leads.firstOrNull()?.id ?: 0L)
    }
    val selectedLead = uiState.leads.find { it.id == selectedLeadId } ?: uiState.leads.firstOrNull()

    val tones = listOf("Warm & Consultative", "Urgent / Few Units Left", "Luxury Exclusive", "Investor Yield")
    var selectedTone by remember { mutableStateOf(tones[0]) }

    var expandedObjectionIndex by remember { mutableStateOf<Int?>(0) }

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REAL ESTATE COPILOT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "AI Sales Playbook",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tailored follow-up messaging, real estate objection counters, and conversion playbooks.",
                        fontSize = 12.sp,
                        color = Color(0xCCFFFFFF)
                    )
                }
            }
        }

        // Section 1: AI Pitch Generator Testbed
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = NavyPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Follow-up Composer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Generates high-converting outreach personalized to buyer's budget and stage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Lead Selector Pills
                    Text(
                        text = "SELECT CLIENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.leads.take(4).forEach { lead ->
                            FilterChip(
                                selected = selectedLead?.id == lead.id,
                                onClick = { selectedLeadId = lead.id },
                                label = { Text(lead.name.split(" ")[0], fontSize = 11.sp) }
                            )
                        }
                    }

                    if (selectedLead != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "Selected: ${selectedLead.name} (${selectedLead.stage.label}) • Looking for ${selectedLead.bhk} ${selectedLead.propertyType} in ${selectedLead.preferredLocation}",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tone Selector
                    Text(
                        text = "MESSAGING TONE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tones.forEach { tone ->
                            FilterChip(
                                selected = selectedTone == tone,
                                onClick = { selectedTone = tone },
                                label = { Text(tone, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Generate Button
                    ElevatedButton(
                        onClick = {
                            if (selectedLead != null) {
                                onGeneratePitch(selectedLead, "Skyline Summit Residences", selectedTone)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_pitch_button"),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = GoldAccent,
                            contentColor = NavyPrimary
                        ),
                        enabled = !uiState.isAiPitchGenerating && selectedLead != null
                    ) {
                        if (uiState.isAiPitchGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crafting Pitch...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Tailored Pitch", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Result Display Box
                    if (!uiState.aiPitch.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFFBEB))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "RECOMMENDED PITCH",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        letterSpacing = 0.5.sp
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        OutlinedButton(
                                            onClick = { copyToClipboard(context, uiState.aiPitch) },
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy", fontSize = 10.sp)
                                        }

                                        if (selectedLead != null) {
                                            FilledTonalButton(
                                                onClick = { launchMessage(context, selectedLead.phone, selectedLead.name) },
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Send", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.aiPitch,
                                    fontSize = 13.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Real Estate Objection Handler
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Real Estate Objection Handling Guide",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Tested scripts & psychological frameworks for standard buyer hesitations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(items = uiState.objectionPlaybook.indices.toList(), key = { "obj_$it" }) { idx ->
            val obj = uiState.objectionPlaybook[idx]
            val isExpanded = expandedObjectionIndex == idx

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable {
                        expandedObjectionIndex = if (isExpanded) null else idx
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "“${obj.objectionTitle}”",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = NavyPrimary
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            // Talk Track
                            Text(
                                text = "WHAT TO SAY:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0FDF4),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = obj.talkTrack,
                                    fontSize = 12.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Agent Tip
                            Text(
                                text = "COACHING TIP:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFFBEB),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = obj.agentTip,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E),
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
