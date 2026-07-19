package com.example.smartfaqai.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.smartfaqai.data.FaqRepository
import com.example.smartfaqai.data.entity.ChatMessageEntity
import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.ui.SavedAnswerItem

class HomeViewModel(repository: FaqRepository) : ViewModel() {
    val suggested: LiveData<List<FaqEntity>> = repository.observeSuggested()

    val recent: LiveData<List<SavedAnswerItem>> = MediatorLiveData<List<SavedAnswerItem>>().apply {
        addSource(repository.observeHistory()) { messages ->
            value = toRecentConversations(messages).take(3)
        }
    }

    private fun toRecentConversations(messages: List<ChatMessageEntity>): List<SavedAnswerItem> =
        messages.groupBy { it.conversationId }.mapNotNull { (conversationId, conversation) ->
            val sorted = conversation.sortedWith(
                compareBy<ChatMessageEntity> { it.createdAt }.thenBy { it.id }
            )
            val questionIndex = sorted.indexOfFirst { it.isUser }
            if (questionIndex < 0) return@mapNotNull null
            val question = sorted[questionIndex]
            val answer = sorted.drop(questionIndex + 1)
                .firstOrNull { !it.isUser } ?: return@mapNotNull null
            SavedAnswerItem(
                key = conversationId,
                question = question.text,
                answer = answer.text,
                timestamp = sorted.maxOf { it.createdAt }
            )
        }.sortedByDescending { it.timestamp }
}
