package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.ui.theme.ColdLeadBlue
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.LostGray
import com.example.ui.theme.WarmLeadOrange
import com.example.ui.theme.WonGreen

@Composable
fun StageBadge(stage: LeadStage, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (stage) {
        LeadStage.NEW -> Color(0xFFE0F2FE) to Color(0xFF0369A1)
        LeadStage.CONTACTED -> Color(0xFFE0E7FF) to Color(0xFF4338CA)
        LeadStage.SITE_VISIT_SCHEDULED -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        LeadStage.SITE_VISIT_COMPLETED -> Color(0xFFFDE68A) to Color(0xFF92400E)
        LeadStage.NEGOTIATION -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
        LeadStage.UNDER_CONTRACT -> Color(0xFFF3E8FF) to Color(0xFF7E22CE)
        LeadStage.WON -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        LeadStage.LOST -> Color(0xFFF3F4F6) to LostGray
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = stage.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun PriorityBadge(priority: LeadPriority, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (priority) {
        LeadPriority.HOT -> Color(0xFFFEE2E2) to HotLeadRed
        LeadPriority.WARM -> Color(0xFFFFEDD5) to WarmLeadOrange
        LeadPriority.COLD -> Color(0xFFDBEAFE) to ColdLeadBlue
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (priority == LeadPriority.HOT) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = HotLeadRed
            )
        }
        Text(
            text = priority.label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun FinancingBadge(status: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF475569)
        )
    }
}
