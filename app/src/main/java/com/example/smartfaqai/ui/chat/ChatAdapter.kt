package com.example.smartfaqai.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfaqai.databinding.ItemMessageBotBinding
import com.example.smartfaqai.databinding.ItemMessageUserBinding
import java.text.DateFormat
import java.util.Date

class ChatAdapter(
    private val onCopy: (UiMessage) -> Unit,
    private val onShare: (UiMessage) -> Unit,
    private val onFavorite: (UiMessage) -> Unit
) : ListAdapter<UiMessage, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<UiMessage>() {
        override fun areItemsTheSame(oldItem: UiMessage, newItem: UiMessage) =
            oldItem.timestamp == newItem.timestamp && oldItem.isUser == newItem.isUser

        override fun areContentsTheSame(oldItem: UiMessage, newItem: UiMessage) = oldItem == newItem
    }
) {
    override fun getItemViewType(position: Int) = if (getItem(position).isUser) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 1) {
            UserHolder(ItemMessageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BotHolder(ItemMessageBotBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserHolder -> holder.bind(getItem(position))
            is BotHolder -> holder.bind(getItem(position))
        }
    }

    private fun formatTime(value: Long): String =
        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(value))

    inner class UserHolder(private val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UiMessage) {
            binding.messageText.text = item.text
            binding.timeText.text = formatTime(item.timestamp)
        }
    }

    inner class BotHolder(private val binding: ItemMessageBotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UiMessage) {
            binding.messageText.text = item.text
            binding.timeText.text = formatTime(item.timestamp)
            binding.copyButton.setOnClickListener { onCopy(item) }
            binding.shareButton.setOnClickListener { onShare(item) }
            binding.favoriteButton.isEnabled = item.matchedFaqId != null
            binding.favoriteButton.alpha = if (item.matchedFaqId == null) 0.35f else 1f
            binding.favoriteButton.setOnClickListener { onFavorite(item) }
        }
    }
}
