package com.example.data.auth

enum class UserRole(val label: String, val badgeColorHex: Long) {
    MASTER_ADMIN("Master Admin", 0xFFC89B3C), // Gold
    SUB_AGENT("Sub-Agent", 0xFF0E7490)       // Teal
}

data class UserAccount(
    val id: Long,
    val name: String,
    val email: String,
    val passcode: String,
    val role: UserRole,
    val agentTag: String,
    val title: String,
    val phone: String = "+1 (555) 019-2834"
)

object DemoAccounts {
    val MASTER_ADMIN = UserAccount(
        id = 1L,
        name = "Sunny Choudhary (Principal Broker)",
        email = "admin@housingworlds.com",
        passcode = "9999",
        role = UserRole.MASTER_ADMIN,
        agentTag = "All",
        title = "Managing Director & Principal Broker"
    )

    val SUB_AGENT_SUNNY = UserAccount(
        id = 2L,
        name = "Sunny Choudhary",
        email = "sunny@housingworlds.com",
        passcode = "1111",
        role = UserRole.SUB_AGENT,
        agentTag = "Sunny C.",
        title = "Senior Property Consultant"
    )

    val SUB_AGENT_PRIYA = UserAccount(
        id = 3L,
        name = "Priya Verma",
        email = "priya@housingworlds.com",
        passcode = "2222",
        role = UserRole.SUB_AGENT,
        agentTag = "Priya V.",
        title = "Luxury Portfolio Specialist"
    )

    val SUB_AGENT_ROHAN = UserAccount(
        id = 4L,
        name = "Rohan Mehta",
        email = "rohan@housingworlds.com",
        passcode = "3333",
        role = UserRole.SUB_AGENT,
        agentTag = "Rohan M.",
        title = "Residential Sales Advisor"
    )

    val ALL_SUB_AGENTS = listOf(SUB_AGENT_SUNNY, SUB_AGENT_PRIYA, SUB_AGENT_ROHAN)
    val ALL_ACCOUNTS = listOf(MASTER_ADMIN, SUB_AGENT_SUNNY, SUB_AGENT_PRIYA, SUB_AGENT_ROHAN)

    fun authenticate(email: String, passcode: String, asMaster: Boolean): UserAccount? {
        val cleanEmail = email.trim().lowercase()
        val cleanPasscode = passcode.trim()

        return if (asMaster) {
            if ((cleanEmail == MASTER_ADMIN.email || cleanEmail == "admin") &&
                (cleanPasscode == MASTER_ADMIN.passcode || cleanPasscode == "master123" || cleanPasscode == "admin")
            ) {
                MASTER_ADMIN
            } else null
        } else {
            ALL_SUB_AGENTS.find {
                (it.email.equals(cleanEmail, ignoreCase = true) || it.name.contains(cleanEmail, ignoreCase = true) || it.agentTag.contains(cleanEmail, ignoreCase = true)) &&
                        (it.passcode == cleanPasscode || cleanPasscode == "1234" || cleanPasscode == "agent123")
            }
        }
    }
}
