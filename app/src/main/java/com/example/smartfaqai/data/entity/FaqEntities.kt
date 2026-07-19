package com.example.smartfaqai.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "faqs", indices = [Index(value = ["category"]), Index(value = ["normalizedQuestion"], unique = true)])
data class FaqEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val normalizedQuestion: String,
    val answer: String,
    val category: String
)

@Entity(tableName = "chat_messages", indices = [Index(value = ["conversationId"]), Index(value = ["createdAt"])])
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val text: String,
    val isUser: Boolean,
    val faqId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites", indices = [Index(value = ["faqId"], unique = true)])
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val faqId: Long,
    val question: String,
    val answer: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)
