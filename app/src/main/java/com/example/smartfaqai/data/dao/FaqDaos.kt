package com.example.smartfaqai.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartfaqai.data.entity.ChatMessageEntity
import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.data.entity.FavoriteEntity

@Dao
interface FaqDao {
    @Query("SELECT COUNT(*) FROM faqs")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FaqEntity>)

    @Query("SELECT * FROM faqs ORDER BY id ASC")
    suspend fun getAll(): List<FaqEntity>

    @Query("SELECT * FROM faqs ORDER BY id ASC LIMIT :limit")
    fun observeSuggested(limit: Int = 6): LiveData<List<FaqEntity>>

    @Query("SELECT * FROM faqs WHERE category = :category ORDER BY id ASC")
    fun observeByCategory(category: String): LiveData<List<FaqEntity>>

    @Query(
        """
        SELECT * FROM faqs
        WHERE question LIKE '%' || :query || '%'
           OR answer LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
        ORDER BY id ASC
        """
    )
    fun search(query: String): LiveData<List<FaqEntity>>

    @Query("SELECT * FROM faqs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FaqEntity?
}

@Dao
interface ChatHistoryDao {
    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY createdAt DESC, id DESC")
    fun observeAll(): LiveData<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY createdAt ASC, id ASC")
    fun observeConversation(conversationId: String): LiveData<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY createdAt ASC, id ASC")
    suspend fun getConversation(conversationId: String): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun clear()
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(favorite: FavoriteEntity): Long

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun observeAll(): LiveData<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE faqId = :faqId")
    suspend fun deleteByFaqId(faqId: Long)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM favorites")
    suspend fun clear()
}
