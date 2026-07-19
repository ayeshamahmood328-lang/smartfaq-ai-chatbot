package com.example.smartfaqai.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfaqai.data.entity.FaqEntity
import com.example.smartfaqai.databinding.ItemQuestionBinding
import com.example.smartfaqai.databinding.ItemSavedAnswerBinding
import java.text.DateFormat
import java.util.Date

data class SavedAnswerItem(
    val key: String,
    val question: String,
    val answer: String,
    val timestamp: Long,
    val faqId: Long? = null,
    val category: String = ""
)

class QuestionAdapter(private val onClick: (FaqEntity) -> Unit) :
    ListAdapter<FaqEntity, QuestionAdapter.Holder>(object : DiffUtil.ItemCallback<FaqEntity>() {
        override fun areItemsTheSame(oldItem: FaqEntity, newItem: FaqEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FaqEntity, newItem: FaqEntity) = oldItem == newItem
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FaqEntity) {
            binding.questionText.text = item.question
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}

class SavedAnswerAdapter(
    private val onOpen: (SavedAnswerItem) -> Unit,
    private val onDelete: (SavedAnswerItem) -> Unit
) : ListAdapter<SavedAnswerItem, SavedAnswerAdapter.Holder>(
    object : DiffUtil.ItemCallback<SavedAnswerItem>() {
        override fun areItemsTheSame(oldItem: SavedAnswerItem, newItem: SavedAnswerItem) =
            oldItem.key == newItem.key

        override fun areContentsTheSame(oldItem: SavedAnswerItem, newItem: SavedAnswerItem) =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemSavedAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    fun itemAt(position: Int): SavedAnswerItem = getItem(position)

    inner class Holder(private val binding: ItemSavedAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SavedAnswerItem) {
            binding.questionText.text = item.question
            binding.answerText.text = item.answer
            binding.timeText.text =
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(item.timestamp))
            binding.root.setOnClickListener { onOpen(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
        }
    }
}
