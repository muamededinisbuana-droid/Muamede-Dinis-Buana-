package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.ChatMessageEntity
import com.example.data.KnowledgeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Buana AI", appName)
  }

  @Test
  fun `verify child protection emergency contacts`() {
    val contacts = KnowledgeRepository.emergencyContacts
    assertTrue(contacts.isNotEmpty())
    val falaCrianca = contacts.firstOrNull { it.number == "116" }
    assertNotNull(falaCrianca)
    assertEquals("Linha Fala Criança", falaCrianca?.name)
  }

  @Test
  fun `verify quick guides contains english and child protection`() {
    val guides = KnowledgeRepository.quickGuides
    assertTrue(guides.any { it.id == "english_basics" })
    assertTrue(guides.any { it.id == "child_protection" })
    assertTrue(guides.any { it.id == "psychosocial_support" })
    assertTrue(guides.any { it.id == "educational_guidance" })
    assertTrue(guides.any { it.id == "ximakonde_phrases" })
  }

  @Test
  fun `verify Room chat history persistence`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    val dao = db.chatDao()

    dao.clearHistory()
    val initialList = dao.getAllMessages().first()
    assertTrue(initialList.isEmpty())

    val userMsg = ChatMessageEntity(
      sender = "user",
      content = "Hello, can you help me learn English?",
      language = "pt",
      category = "english"
    )
    val modelMsg = ChatMessageEntity(
      sender = "model",
      content = "Yes! I would love to help you.",
      language = "pt",
      category = "english"
    )

    dao.insertMessage(userMsg)
    dao.insertMessage(modelMsg)

    val persisted = dao.getAllMessages().first()
    assertEquals(2, persisted.size)
    assertEquals("user", persisted[0].sender)
    assertEquals("Hello, can you help me learn English?", persisted[0].content)
    assertEquals("model", persisted[1].sender)
  }
}
