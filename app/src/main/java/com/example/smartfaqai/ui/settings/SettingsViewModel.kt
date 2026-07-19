package com.example.smartfaqai.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfaqai.data.FaqRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: FaqRepository) : ViewModel() {
    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}
