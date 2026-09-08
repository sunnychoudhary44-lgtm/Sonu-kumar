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
import androidx.compose.foundation.verticalScroll
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
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.NavyPrimary

@Composable
fun AddEditLeadDialog(
    leadToEdit: LeadEntity?,
    onDismiss: () -> Unit,
    onSave: (LeadEntity) -> Unit
) {
    var name by remember { mutableStateOf(leadToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(leadToEdit?.phone ?: "") }
    var email by remember { mutableStateOf(leadToEdit?.email ?: "") }
    var propertyType by remember { mutableStateOf(leadToEdit?.propertyType ?: "Apartment") }
    var bhk by remember { mutableStateOf(leadToEdit?.bhk ?: "3 BHK") }
    var budgetMinText by remember { mutableStateOf(leadToEdit?.budgetMin?.toInt()?.toString() ?: "350000") }
    var budgetMaxText by remember { mutableStateOf(leadToEdit?.budgetMax?.toInt()?.toString() ?: "500000") }
    var preferredLocation by remember { mutableStateOf(leadToEdit?.preferredLocation ?: "Downtown Core") }
    var stage by remember { mutableStateOf(leadToEdit?.stage ?: LeadStage.NEW) }
    var priority by remember { mutableStateOf(leadToEdit?.priority ?: LeadPriority.WARM) }
    var source by remember { mutableStateOf(leadToEdit?.source ?: "Website Form") }
    var financing by remember { mutableStateOf(leadToEdit?.financingStatus ?: "Pre-approved") }
    var notes by remember { mutableStateOf(leadToEdit?.notes ?: "") }
    var followUpNote by remember { mutableStateOf(leadToEdit?.nextFollowUpNote ?: "") }

    var hasError by remember { mutableStateOf(false) }

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
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (leadToEdit == null) "Add New Real Estate Lead" else "Edit Lead Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Capture buyer profile, budget range and property requirements",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; hasError = false },
                    label = { Text("Client Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("lead_name_input"),
                    singleLine = true,
                    isError = hasError && name.isBlank()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone & Email
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Property Type Chips
                Text("PROPERTY TYPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Apartment", "Villa", "Penthouse", "Commercial", "Plot").forEach { type ->
                        FilterChip(
                            selected = propertyType == type,
                            onClick = { propertyType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                // BHK Chips
                Text("BEDROOMS / CONFIG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1 BHK", "2 BHK", "3 BHK", "4+ BHK", "Studio", "Commercial").forEach { b ->
                        FilterChip(
                            selected = bhk == b,
                            onClick = { bhk = b },
                            label = { Text(b, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Budget Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = budgetMinText,
                        onValueChange = { budgetMinText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Min Budget ($)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = budgetMaxText,
                        onValueChange = { budgetMaxText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Max Budget ($)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Preferred Location
                OutlinedTextField(
                    value = preferredLocation,
                    onValueChange = { preferredLocation = it },
                    label = { Text("Preferred Location / Sector") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Priority
                Text("LEAD PRIORITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LeadPriority.values().forEach { prio ->
                        FilterChip(
                            selected = priority == prio,
                            onClick = { priority = prio },
                            label = { Text(prio.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (prio == LeadPriority.HOT) HotLeadRed else GoldAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Lead Source
                Text("SOURCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Website Form", "MagicBricks", "Referral", "Meta Ad", "Direct Call", "Walk-in").forEach { src ->
                        FilterChip(
                            selected = source == src,
                            onClick = { source = src },
                            label = { Text(src, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Financing
                OutlinedTextField(
                    value = financing,
                    onValueChange = { financing = it },
                    label = { Text("Financing (e.g. Pre-approved, Cash, Bank Loan)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Buyer Notes / Preferences") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Next Follow-up note
                OutlinedTextField(
                    value = followUpNote,
                    onValueChange = { followUpNote = it },
                    label = { Text("Next Follow-up Task (Optional)") },
                    placeholder = { Text("e.g. Host site visit Saturday 2pm") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
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
                            if (name.isBlank()) {
                                hasError = true
                                return@Button
                            }
                            val bMin = budgetMinText.toDoubleOrNull() ?: 300000.0
                            val bMax = budgetMaxText.toDoubleOrNull() ?: 500000.0

                            val lead = LeadEntity(
                                id = leadToEdit?.id ?: 0L,
                                name = name.trim(),
                                phone = if (phone.isNotBlank()) phone.trim() else "+1 (555) 000-0000",
                                email = if (email.isNotBlank()) email.trim() else "client@example.com",
                                propertyType = propertyType,
                                bhk = bhk,
                                budgetMin = bMin,
                                budgetMax = bMax,
                                preferredLocation = preferredLocation.trim(),
                                stage = stage,
                                priority = priority,
                                source = source,
                                financingStatus = financing.trim(),
                                assignedAgent = leadToEdit?.assignedAgent ?: "Sunny C.",
                                notes = notes.trim(),
                                createdAt = leadToEdit?.createdAt ?: System.currentTimeMillis(),
                                lastContactedAt = System.currentTimeMillis(),
                                nextFollowUpDate = if (followUpNote.isNotBlank()) System.currentTimeMillis() + 86400000L else leadToEdit?.nextFollowUpDate,
                                nextFollowUpNote = if (followUpNote.isNotBlank()) followUpNote.trim() else leadToEdit?.nextFollowUpNote
                            )
                            onSave(lead)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = NavyPrimary
                        ),
                        modifier = Modifier.testTag("save_lead_button")
                    ) {
                        Text("Save Lead", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
