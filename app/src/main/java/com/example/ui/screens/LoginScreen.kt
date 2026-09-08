package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.DemoAccounts
import com.example.data.auth.UserAccount
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

@Composable
fun LoginScreen(
    onLogin: (email: String, passcode: String, asMaster: Boolean) -> Boolean,
    onQuickLogin: (UserAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Master Login, 1 = Sub-Agent Login
    var selectedTab by remember { mutableIntStateOf(0) }

    // Master form state
    var masterEmail by remember { mutableStateOf(DemoAccounts.MASTER_ADMIN.email) }
    var masterPasscode by remember { mutableStateOf("9999") }

    // Sub-agent form state
    var selectedSubAgent by remember { mutableStateOf(DemoAccounts.SUB_AGENT_SUNNY) }
    var subEmail by remember { mutableStateOf(DemoAccounts.SUB_AGENT_SUNNY.email) }
    var subPasscode by remember { mutableStateOf("1111") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NavyPrimary,
                        NavyLight,
                        Color(0xFF070E18)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GoldAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apartment,
                    contentDescription = "Housing Worlds",
                    tint = NavyPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "HOUSING WORLDS",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Text(
                text = "Multi-Tier Real Estate Lead CRM",
                fontSize = 13.sp,
                color = GoldAccent,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Master vs Sub Login Tab Selector
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x33FFFFFF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Master Tab Button
                    val isMasterSelected = selectedTab == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isMasterSelected) GoldAccent else Color.Transparent)
                            .clickable {
                                selectedTab = 0
                                errorMessage = null
                            }
                            .padding(vertical = 12.dp)
                            .testTag("login_tab_master"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (isMasterSelected) NavyPrimary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Master Login",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isMasterSelected) NavyPrimary else Color.White
                            )
                        }
                    }

                    // Sub-Agent Tab Button
                    val isSubSelected = selectedTab == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSubSelected) TealAccent else Color.Transparent)
                            .clickable {
                                selectedTab = 1
                                errorMessage = null
                            }
                            .padding(vertical = 12.dp)
                            .testTag("login_tab_sub"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sub-Agent Login",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    if (selectedTab == 0) {
                        // MASTER LOGIN FORM
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Principal Broker Portal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Full CRM control & team visibility",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Features Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFFBEB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "MASTER PRIVILEGES:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                                Text(
                                    text = "• View & reassign leads across all agents\n• Full agency analytics & closed revenue\n• Inventory controls & property brochure edits",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = masterEmail,
                            onValueChange = { masterEmail = it; errorMessage = null },
                            label = { Text("Master Admin Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NavyPrimary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("master_email_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = masterPasscode,
                            onValueChange = { masterPasscode = it; errorMessage = null },
                            label = { Text("Master Passcode / PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("master_pin_input")
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val success = onLogin(masterEmail, masterPasscode, true)
                                if (!success) {
                                    errorMessage = "Invalid Master credentials (PIN: 9999)"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("master_login_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = NavyPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Sign In as Master Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Fill Master
                        OutlinedButton(
                            onClick = { onQuickLogin(DemoAccounts.MASTER_ADMIN) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_master_login"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚡ 1-Tap Master Demo Login (9999)", fontSize = 12.sp)
                        }

                    } else {
                        // SUB-AGENT LOGIN FORM
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = TealAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sub-Agent CRM Portal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Agent pipeline & client follow-ups",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "CHOOSE AGENT ACCOUNT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Agent Select Cards
                        DemoAccounts.ALL_SUB_AGENTS.forEach { agent ->
                            val isSelected = selectedSubAgent.id == agent.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFE0F2FE) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable {
                                        selectedSubAgent = agent
                                        subEmail = agent.email
                                        subPasscode = agent.passcode
                                        errorMessage = null
                                    }
                                    .testTag("select_sub_agent_${agent.agentTag.replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(TealAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = agent.agentTag.take(2),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = agent.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${agent.title} • PIN: ${agent.passcode}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = TealAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = subEmail,
                            onValueChange = { subEmail = it; errorMessage = null },
                            label = { Text("Agent Email") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealAccent) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sub_email_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = subPasscode,
                            onValueChange = { subPasscode = it; errorMessage = null },
                            label = { Text("Agent PIN") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = TealAccent) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sub_pin_input")
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val success = onLogin(subEmail, subPasscode, false)
                                if (!success) {
                                    errorMessage = "Invalid credentials for ${selectedSubAgent.name}"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("sub_login_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealAccent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Sign In as ${selectedSubAgent.agentTag}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { onQuickLogin(selectedSubAgent) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_sub_login"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚡ 1-Tap Sign In as ${selectedSubAgent.name}", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Credentials helper
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x1FFFFFFF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Demo Passcodes: Master Admin (PIN: 9999) • Sunny C. (1111) • Priya V. (2222) • Rohan M. (3333)",
                        fontSize = 11.sp,
                        color = Color(0xCCFFFFFF),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
