package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.NavyPrimary

@Composable
fun LeadFilterChipRow(
    allLeads: List<LeadEntity>,
    selectedStage: LeadStage?,
    onStageSelected: (LeadStage?) -> Unit,
    selectedPriority: LeadPriority?,
    onPrioritySelected: (LeadPriority?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All Leads" chip
        FilterChip(
            selected = selectedStage == null && selectedPriority == null,
            onClick = {
                onStageSelected(null)
                onPrioritySelected(null)
            },
            label = {
                Text(text = "All (${allLeads.size})", fontSize = 12.sp)
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NavyPrimary,
                selectedLabelColor = androidx.compose.ui.graphics.Color.White
            ),
            modifier = Modifier.testTag("filter_chip_all")
        )

        // Hot Leads Quick Filter
        val hotCount = allLeads.count { it.priority == LeadPriority.HOT }
        FilterChip(
            selected = selectedPriority == LeadPriority.HOT,
            onClick = {
                if (selectedPriority == LeadPriority.HOT) onPrioritySelected(null)
                else onPrioritySelected(LeadPriority.HOT)
            },
            label = {
                Text(text = "🔥 Hot ($hotCount)", fontSize = 12.sp)
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = HotLeadRed,
                selectedLabelColor = androidx.compose.ui.graphics.Color.White
            ),
            modifier = Modifier.testTag("filter_chip_hot")
        )

        // Pipeline Stages
        LeadStage.values().forEach { stage ->
            val count = allLeads.count { it.stage == stage }
            FilterChip(
                selected = selectedStage == stage,
                onClick = {
                    if (selectedStage == stage) onStageSelected(null)
                    else onStageSelected(stage)
                },
                label = {
                    Text(text = "${stage.label} ($count)", fontSize = 12.sp)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldAccent,
                    selectedLabelColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.testTag("filter_chip_${stage.name.lowercase()}")
            )
        }
    }
}
