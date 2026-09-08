package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import com.example.ui.components.LeadCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun lead_card_screenshot() {
    val sampleLead = LeadEntity(
      id = 1L,
      name = "Vikramaditya Sharma",
      phone = "+1 (555) 234-5678",
      email = "vikram@example.com",
      propertyType = "Apartment",
      bhk = "3 BHK",
      budgetMin = 450000.0,
      budgetMax = 580000.0,
      preferredLocation = "Downtown Core",
      stage = LeadStage.SITE_VISIT_SCHEDULED,
      priority = LeadPriority.HOT,
      source = "MagicBricks",
      financingStatus = "Pre-approved",
      assignedAgent = "Sunny C.",
      notes = "Looking for high-floor unit with city skyline view",
      createdAt = System.currentTimeMillis(),
      lastContactedAt = System.currentTimeMillis(),
      nextFollowUpDate = System.currentTimeMillis() + 86400000L,
      nextFollowUpNote = "Site visit booked Saturday 3 PM"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        LeadCard(
          lead = sampleLead,
          onClick = {},
          onLogActivity = {},
          onAdvanceStage = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/lead_card.png")
  }
}
