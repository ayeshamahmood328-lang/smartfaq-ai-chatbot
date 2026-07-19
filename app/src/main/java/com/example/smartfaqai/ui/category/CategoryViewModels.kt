package com.example.smartfaqai.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfaqai.data.FaqRepository
import com.example.smartfaqai.data.entity.FaqEntity
import kotlinx.coroutines.launch
import java.util.UUID

class CategoryViewModel(private val repository: FaqRepository) : ViewModel() {
    private val query = MutableLiveData("")
    private val allFaqs = MutableLiveData<List<FaqEntity>>(emptyList())
    private val _faqs = MediatorLiveData<List<FaqEntity>>()
    val faqs: LiveData<List<FaqEntity>> = _faqs

    private var categorySource: LiveData<List<FaqEntity>>? = null

    init {
        _faqs.addSource(query) { filter() }
        _faqs.addSource(allFaqs) { filter() }
    }

    fun loadCategory(category: String) {
        categorySource?.let(_faqs::removeSource)
        categorySource = repository.observeByCategory(category).also { source ->
            _faqs.addSource(source) { allFaqs.value = it }
        }
        viewModelScope.launch { repository.ensureSeeded() }
    }

    fun search(value: String) {
        query.value = value
    }

    private fun filter() {
        val text = query.value.orEmpty().trim()
        val source = allFaqs.value.orEmpty()
        _faqs.value = if (text.isBlank()) {
            source
        } else {
            source.filter {
                it.question.contains(text, ignoreCase = true) ||
                    it.answer.contains(text, ignoreCase = true)
            }
        }
    }
}

class FaqDetailViewModel(private val repository: FaqRepository) : ViewModel() {
    private val _faq = MutableLiveData<FaqEntity?>()
    val faq: LiveData<FaqEntity?> = _faq
    private var loadedId: Long? = null

    fun load(faqId: Long) {
        if (loadedId == faqId) return
        loadedId = faqId
        viewModelScope.launch {
            val item = repository.getFaq(faqId)
            _faq.value = item
            if (item != null) saveToHistory(item)
        }
    }

    fun favorite() {
        _faq.value?.let { item ->
            viewModelScope.launch { repository.addFavorite(item) }
        }
    }

    private suspend fun saveToHistory(item: FaqEntity) {
        val conversationId = "faq-${item.id}-${UUID.randomUUID()}"
        repository.saveMessage(conversationId, item.question, isUser = true, faqId = item.id)
        repository.saveMessage(conversationId, item.answer, isUser = false, faqId = item.id)
    }
}
