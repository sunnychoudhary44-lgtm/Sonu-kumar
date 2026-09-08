package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.LeadDao
import com.example.data.local.LeadEntity
import com.example.data.local.LeadPriority
import com.example.data.local.LeadStage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase
  private lateinit var leadDao: LeadDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    leadDao = db.leadDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Housing Worlds", appName)
  }

  @Test
  fun `insert and retrieve lead from Room database`() = runBlocking {
    val lead = LeadEntity(
      id = 1L,
      name = "Vikram Sharma",
      phone = "+1 555-1234",
      email = "vikram@example.com",
      propertyType = "Apartment",
      bhk = "3 BHK",
      budgetMin = 400000.0,
      budgetMax = 600000.0,
      preferredLocation = "Downtown",
      stage = LeadStage.NEW,
      priority = LeadPriority.HOT,
      source = "Website",
      financingStatus = "Pre-approved",
      assignedAgent = "Sunny C.",
      notes = "Immediate requirement"
    )

    leadDao.insertLead(lead)
    val leads = leadDao.getAllLeads().first()

    assertEquals(1, leads.size)
    assertEquals("Vikram Sharma", leads[0].name)
    assertEquals(LeadPriority.HOT, leads[0].priority)
  }

  @Test
  fun `verify master login and sub login authentication`() {
    val master = com.example.data.auth.DemoAccounts.authenticate("admin@housingworlds.com", "9999", asMaster = true)
    assertTrue(master != null)
    assertEquals(com.example.data.auth.UserRole.MASTER_ADMIN, master?.role)

    val subAgent = com.example.data.auth.DemoAccounts.authenticate("sunny@housingworlds.com", "1111", asMaster = false)
    assertTrue(subAgent != null)
    assertEquals(com.example.data.auth.UserRole.SUB_AGENT, subAgent?.role)
    assertEquals("Sunny C.", subAgent?.agentTag)

    val invalidMaster = com.example.data.auth.DemoAccounts.authenticate("admin@housingworlds.com", "0000", asMaster = true)
    assertTrue(invalidMaster == null)
  }
}

