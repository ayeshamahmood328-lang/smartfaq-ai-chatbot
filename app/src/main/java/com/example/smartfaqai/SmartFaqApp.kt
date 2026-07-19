package com.example.smartfaqai

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.smartfaqai.data.AppDatabase
import com.example.smartfaqai.data.FaqRepository
import com.example.smartfaqai.util.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmartFaqApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val repository: FaqRepository by lazy {
        FaqRepository(AppDatabase.getInstance(this))
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
            if (Prefs.isDarkMode(this)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        appScope.launch { repository.ensureSeeded() }
    }
}
