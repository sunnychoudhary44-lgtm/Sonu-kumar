package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.ActivityType
import com.example.data.local.LeadEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun LogActivityDialog(
    lead: LeadEntity,
    onDismiss: () -> Unit,
    onLog: (ActivityType, String, String?, Long?, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(ActivityType.CALL) }
    var summary by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf("") }
    var nextFollowUpNote by remember { mutableStateOf("") }

    val quickOutcomes = listOf(
        "Interested & requested follow-up",
        "Scheduled site visit",
        "Unreachable / left voicemail",
        "Requested price sheet & brochure",
        "Negotiating payment terms"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Log Activity with ${lead.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Record touchpoint details to maintain full client history",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Activity Type Chips
                Text("ACTIVITY TYPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        ActivityType.CALL,
                        ActivityType.WHATSAPP,
                        ActivityType.SITE_VISIT,
                        ActivityType.MEETING,
                        ActivityType.PROPOSAL,
                        ActivityType.NOTE
                    ).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldAccent,
                                selectedLabelColor = NavyPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Summary input
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Interaction Summary *") },
                    placeholder = { Text("e.g. Called to discuss 3BHK layout & price") },
                    modifier = Modifier.fillMaxWidth().testTag("activity_summary_input"),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Outcome suggestions
                Text("OUTCOME / DISPOSITION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickOutcomes.forEach { q ->
                        FilterChip(
                            selected = outcome == q,
                            onClick = { outcome = q },
                            label = { Text(q, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = outcome,
                    onValueChange = { outcome = it },
                    label = { Text("Outcome or Client Reaction") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Next Follow Up Task
                OutlinedTextField(
                    value = nextFollowUpNote,
                    onValueChange = { nextFollowUpNote = it },
                    label = { Text("Next Follow-up Action") },
                    placeholder = { Text("e.g. Call back Tuesday with builder approval") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalSummary = if (summary.isNotBlank()) summary.trim() else "Logged ${selectedType.label}"
                            val followUpTime = if (nextFollowUpNote.isNotBlank()) System.currentTimeMillis() + 86400000L else null
                            onLog(
                                selectedType,
                                finalSummary,
                                if (outcome.isNotBlank()) outcome.trim() else null,
                                followUpTime,
                                if (nextFollowUpNote.isNotBlank()) nextFollowUpNote.trim() else null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("submit_activity_button")
                    ) {
                        Text("Save Activity", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
