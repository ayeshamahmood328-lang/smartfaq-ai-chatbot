package com.example.smartfaqai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.data.FaqRepository
import com.example.smartfaqai.ui.category.CategoryViewModel
import com.example.smartfaqai.ui.category.FaqDetailViewModel
import com.example.smartfaqai.ui.chat.ChatViewModel
import com.example.smartfaqai.ui.favorites.FavoritesViewModel
import com.example.smartfaqai.ui.history.HistoryViewModel
import com.example.smartfaqai.ui.home.HomeViewModel
import com.example.smartfaqai.ui.settings.SettingsViewModel

class ViewModelFactory(
    private val repository: FaqRepository,
    private val chatConversationId: String? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
        modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(repository) as T
        modelClass.isAssignableFrom(FaqDetailViewModel::class.java) -> FaqDetailViewModel(repository) as T
        modelClass.isAssignableFrom(ChatViewModel::class.java) ->
            ChatViewModel(repository, requireNotNull(chatConversationId)) as T
        modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository) as T
        modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(repository) as T
        modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(repository) as T
        else -> error("Unknown ViewModel: ${modelClass.name}")
    }

    companion object {
        fun from(app: SmartFaqApp, chatConversationId: String? = null) =
            ViewModelFactory(app.repository, chatConversationId)
    }
}
