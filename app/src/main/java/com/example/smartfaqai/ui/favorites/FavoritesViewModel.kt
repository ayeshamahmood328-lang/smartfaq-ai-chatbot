package com.example.smartfaqai.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfaqai.data.FaqRepository
import com.example.smartfaqai.ui.SavedAnswerItem
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: FaqRepository) : ViewModel() {
    val items: LiveData<List<SavedAnswerItem>> = MediatorLiveData<List<SavedAnswerItem>>().apply {
        addSource(repository.observeFavorites()) { favorites ->
            value = favorites.map {
                SavedAnswerItem(
                    key = it.id.toString(),
                    question = it.question,
                    answer = it.answer,
                    timestamp = it.createdAt,
                    faqId = it.faqId,
                    category = it.category
                )
            }
        }
    }

    fun delete(item: SavedAnswerItem) {
        item.faqId?.let { faqId ->
            viewModelScope.launch { repository.removeFavoriteByFaqId(faqId) }
        }
    }
}
