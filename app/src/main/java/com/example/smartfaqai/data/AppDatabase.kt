package com.example.smartfaqai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartfaqai.data.dao.ChatHistoryDao
import com.example.smartfaqai.data.dao.FaqDao
import com.example.smartfaqai.data.dao.FavoriteDao
import com.example.smartfaqai.data.entity.ChatMessageEntity
import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.data.entity.FavoriteEntity

@Database(
    entities = [FaqEntity::class, ChatMessageEntity::class, FavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun faqDao(): FaqDao
    abstract fun chatHistoryDao(): ChatHistoryDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_faq.db"
                )
                    // FAQ data is fully re-seeded on open, so wiping on version
                    // bump is safe and guarantees stale seed rows are removed.
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
    }
}
