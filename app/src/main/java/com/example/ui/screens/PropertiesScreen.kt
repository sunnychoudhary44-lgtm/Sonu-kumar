package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.local.PropertyEntity
import com.example.ui.LeadUiState
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StageBadge
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

fun sharePropertyBrochure(context: Context, property: PropertyEntity) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            """
            🏡 *Housing Worlds Exclusive Property*
            *${property.title}* by ${property.projectDeveloper}
            📍 Location: ${property.location}
            📐 Specs: ${property.bhk} • ${property.propertyType} (${property.areaSqFt} sq ft)
            💰 Investment: $${property.price.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")}
            ✨ Highlights: ${property.highlights}
            
            Contact Sunny Choudhary (+1 555-234-5678) at Housing Worlds for private walkthrough bookings!
            """.trimIndent()
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Brochure via"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    uiState: LeadUiState,
    onLeadClick: (LeadEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPropertyForMatching by remember { mutableStateOf<PropertyEntity?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                                    text = "HOUSING WORLDS INVENTORY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Property Catalog",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0x33FFFFFF)
                            ) {
                                Text(
                                    text = "${uiState.properties.size} Projects Active",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Direct inventory matching with buyer preferences & budget parameters.",
                            fontSize = 12.sp,
                            color = Color(0xCCFFFFFF)
                        )
                    }
                }
            }

            // Inventory List
            items(items = uiState.properties, key = { it.id }) { prop ->
                // Calculate matching leads
                val matchingCount = uiState.leads.count { lead ->
                    val typeOrBhk = prop.propertyType.equals(lead.propertyType, true) || prop.bhk.equals(lead.bhk, true)
                    val budget = prop.price >= lead.budgetMin * 0.8 && prop.price <= lead.budgetMax * 1.25
                    typeOrBhk && budget
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("property_card_${prop.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title row & status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prop.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = prop.projectDeveloper,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (prop.status) {
                                    "Ready to Move" -> Color(0xFFDCFCE7)
                                    "Few Units Left" -> Color(0xFFFEE2E2)
                                    "Pre-Launch" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFE0F2FE)
                                }
                            ) {
                                Text(
                                    text = prop.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (prop.status) {
                                        "Ready to Move" -> Color(0xFF15803D)
                                        "Few Units Left" -> Color(0xFFDC2626)
                                        "Pre-Launch" -> Color(0xFFB45309)
                                        else -> Color(0xFF0369A1)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Price & specs badge row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${prop.price.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1,")}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${prop.bhk} • ${prop.propertyType}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.SquareFoot, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${prop.areaSqFt} sq ft", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Location
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = prop.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Highlights Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = prop.highlights,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Match Leads & Share Brochure
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ElevatedButton(
                                onClick = { selectedPropertyForMatching = prop },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = NavyPrimary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$matchingCount Matched Leads",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            OutlinedButton(
                                onClick = { sharePropertyBrochure(context, prop) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Brochure", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Matching Leads Modal Bottom Sheet
        if (selectedPropertyForMatching != null) {
            val prop = selectedPropertyForMatching!!
            val matchedLeads = uiState.leads.filter { lead ->
                val typeOrBhk = prop.propertyType.equals(lead.propertyType, true) || prop.bhk.equals(lead.bhk, true)
                val budget = prop.price >= lead.budgetMin * 0.8 && prop.price <= lead.budgetMax * 1.25
                typeOrBhk && budget
            }

            ModalBottomSheet(
                onDismissRequest = { selectedPropertyForMatching = null },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Matching Leads for ${prop.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Buyers whose budget and property requirements align with this project",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (matchedLeads.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No leads currently match this budget and spec.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(320.dp)
                        ) {
                            items(items = matchedLeads, key = { "match_${it.id}" }) { lead ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
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
                                                text = lead.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${lead.bhk} • Budget: $${lead.budgetMin.toInt()}k-$${lead.budgetMax.toInt()}k",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                selectedPropertyForMatching = null
                                                onLeadClick(lead)
                                            }
                                        ) {
                                            Text("View Lead", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
