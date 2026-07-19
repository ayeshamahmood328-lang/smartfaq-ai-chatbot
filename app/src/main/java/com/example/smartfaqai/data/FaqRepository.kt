package com.example.smartfaqai.data

import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.example.smartfaqai.data.entity.ChatMessageEntity
import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.data.entity.FavoriteEntity
import com.example.smartfaqai.domain.FaqMatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FaqRepository(private val database: AppDatabase) {
    private val faqDao = database.faqDao()
    private val historyDao = database.chatHistoryDao()
    private val favoriteDao = database.favoriteDao()

    fun observeSuggested(): LiveData<List<FaqEntity>> = faqDao.observeSuggested()
    fun observeByCategory(category: String): LiveData<List<FaqEntity>> = faqDao.observeByCategory(category)
    fun searchFaqs(query: String): LiveData<List<FaqEntity>> = faqDao.search(query)
    fun observeHistory(): LiveData<List<ChatMessageEntity>> = historyDao.observeAll()
    fun observeConversation(conversationId: String): LiveData<List<ChatMessageEntity>> =
        historyDao.observeConversation(conversationId)
    fun observeFavorites(): LiveData<List<FavoriteEntity>> = favoriteDao.observeAll()

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val builtInFaqs = FaqSeeder.sampleFaqs()
        if (faqDao.count() < builtInFaqs.size) {
            faqDao.insertAll(builtInFaqs)
        }
    }

    suspend fun findAnswer(question: String): FaqEntity? = withContext(Dispatchers.IO) {
        ensureSeeded()
        FaqMatcher.findBestMatch(question, faqDao.getAll())
    }

    suspend fun getFaq(id: Long): FaqEntity? = withContext(Dispatchers.IO) {
        faqDao.getById(id)
    }

    suspend fun saveMessage(
        conversationId: String,
        text: String,
        isUser: Boolean,
        faqId: Long? = null
    ) = withContext(Dispatchers.IO) {
        historyDao.insert(
            ChatMessageEntity(
                conversationId = conversationId,
                text = text,
                isUser = isUser,
                faqId = faqId
            )
        )
    }

    suspend fun getConversation(conversationId: String): List<ChatMessageEntity> =
        withContext(Dispatchers.IO) {
            historyDao.getConversation(conversationId)
        }

    suspend fun ensureConversationStarted(conversationId: String, welcomeMessage: String) =
        withContext(Dispatchers.IO) {
            database.withTransaction {
                if (historyDao.getConversation(conversationId).isEmpty()) {
                    historyDao.insert(
                        ChatMessageEntity(
                            conversationId = conversationId,
                            text = welcomeMessage,
                            isUser = false
                        )
                    )
                }
            }
        }

    suspend fun deleteConversation(conversationId: String) = withContext(Dispatchers.IO) {
        historyDao.deleteConversation(conversationId)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        historyDao.clear()
    }

    suspend fun addFavorite(faq: FaqEntity) = withContext(Dispatchers.IO) {
        favoriteDao.upsert(
            FavoriteEntity(
                faqId = faq.id,
                question = faq.question,
                answer = faq.answer,
                category = faq.category
            )
        )
    }

    suspend fun removeFavoriteByFaqId(faqId: Long) = withContext(Dispatchers.IO) {
        favoriteDao.deleteByFaqId(faqId)
    }

    suspend fun removeFavorite(id: Long) = withContext(Dispatchers.IO) {
        favoriteDao.deleteById(id)
    }
}
