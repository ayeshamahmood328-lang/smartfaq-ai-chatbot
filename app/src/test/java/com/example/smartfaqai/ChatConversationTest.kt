package com.example.smartfaqai

import com.example.smartfaqai.ui.chat.ChatConversation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ChatConversationTest {
    @Test
    fun categoryId_isStableAcrossCaseAndWhitespace() {
        assertEquals(
            ChatConversation.idForCategory("Account"),
            ChatConversation.idForCategory("  ACCOUNT  ")
        )
    }

    @Test
    fun categoryId_keepsHistoriesSeparate() {
        assertNotEquals(
            ChatConversation.idForCategory("Account"),
            ChatConversation.idForCategory("Payments")
        )
    }

    @Test
    fun blankCategory_usesGeneralConversation() {
        assertEquals(
            ChatConversation.idForCategory(ChatConversation.GENERAL_CATEGORY),
            ChatConversation.idForCategory("")
        )
    }
}
