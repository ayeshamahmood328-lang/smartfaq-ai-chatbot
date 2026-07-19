package com.example.smartfaqai.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfaqai.data.FaqRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class UiMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val matchedFaqId: Long? = null
)

object ChatConversation {
    const val GENERAL_CATEGORY = "General"

    fun idForCategory(category: String): String =
        "category:${category.trim().ifEmpty { GENERAL_CATEGORY }.lowercase(Locale.ROOT)}"
}

class ChatViewModel(
    private val repository: FaqRepository,
    private val conversationId: String
) : ViewModel() {
    val messages: LiveData<List<UiMessage>> =
        repository.observeConversation(conversationId).map { stored ->
            stored.map {
                UiMessage(
                    text = it.text,
                    isUser = it.isUser,
                    timestamp = it.createdAt,
                    matchedFaqId = it.faqId
                )
            }
        }
    val typing = MutableLiveData(false)

    private val initialization = viewModelScope.async {
        repository.ensureConversationStarted(conversationId, WELCOME_MESSAGE)
    }
    private var responseJob: Job? = null

    fun send(rawText: String, noAnswerMessage: String) {
        val text = rawText.trim()
        if (text.isEmpty() || typing.value == true) return

        typing.value = true
        responseJob = viewModelScope.launch {
            initialization.await()
            repository.saveMessage(conversationId, text, isUser = true)
            delay(650)
            val match = runCatching { repository.findAnswer(text) }.getOrNull()
            val answer = match?.answer ?: noAnswerMessage
            repository.saveMessage(conversationId, answer, isUser = false, faqId = match?.id)
            typing.value = false
        }
    }

    fun sendInitialQuestion(rawText: String, noAnswerMessage: String) {
        val text = rawText.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            initialization.await()
            send(text, noAnswerMessage)
        }
    }

    fun clear() {
        responseJob?.cancel()
        typing.value = false
        viewModelScope.launch {
            initialization.await()
            repository.deleteConversation(conversationId)
        }
    }

    fun favorite(message: UiMessage) {
        val faqId = message.matchedFaqId ?: return
        viewModelScope.launch {
            repository.getFaq(faqId)?.let { repository.addFavorite(it) }
        }
    }

    private companion object {
        const val WELCOME_MESSAGE =
            "Hi! I'm SmartFAQ AI. Ask me anything from the FAQ library."
    }
}
