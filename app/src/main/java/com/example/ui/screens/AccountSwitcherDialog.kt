package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.auth.DemoAccounts
import com.example.data.auth.UserAccount
import com.example.data.auth.UserRole
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HotLeadRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

@Composable
fun AccountSwitcherDialog(
    currentUser: UserAccount?,
    onSwitchUser: (UserAccount) -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CRM Account & Role Switcher",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Switch between Master and Sub-Agent profiles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Session Indicator
                if (currentUser != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentUser.role == UserRole.MASTER_ADMIN) Color(0xFFFEF3C7) else Color(0xFFE0F2FE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentUser.role == UserRole.MASTER_ADMIN) GoldAccent else TealAccent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (currentUser.role == UserRole.MASTER_ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Current: ${currentUser.role.label} (${currentUser.title})",
                                    fontSize = 11.sp,
                                    color = if (currentUser.role == UserRole.MASTER_ADMIN) Color(0xFF78350F) else Color(0xFF0369A1),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SWITCH ACCOUNT:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // List all accounts
                DemoAccounts.ALL_ACCOUNTS.forEach { account ->
                    val isActive = currentUser?.id == account.id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isActive) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable {
                                onSwitchUser(account)
                            }
                            .testTag("switch_to_${account.agentTag.replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (account.role == UserRole.MASTER_ADMIN) Color(0xFFFEF3C7) else Color(0xFFE0F2FE)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (account.role == UserRole.MASTER_ADMIN) "👑" else "👤",
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = account.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${account.role.label} • ${account.email}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isActive) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions: Log Out & Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onLogout,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HotLeadRed),
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Out", fontSize = 12.sp)
                    }

                    OutlinedButton(onClick = onDismiss) {
                        Text("Close", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
