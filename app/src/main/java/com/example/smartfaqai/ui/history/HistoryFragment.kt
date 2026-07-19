package com.example.smartfaqai.ui.history

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfaqai.R
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.databinding.FragmentHistoryBinding
import com.example.smartfaqai.ui.SavedAnswerAdapter
import com.example.smartfaqai.ui.SavedAnswerItem
import com.example.smartfaqai.ui.ViewModelFactory
import com.example.smartfaqai.ui.chat.ChatFragment
import kotlin.math.abs

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory.from(requireActivity().application as SmartFaqApp)
    }

    private lateinit var listAdapter: SavedAnswerAdapter
    private var allItems = emptyList<SavedAnswerItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listAdapter = SavedAnswerAdapter(
            onOpen = {
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf(
                        ChatFragment.ARG_QUESTION to it.question,
                        ChatFragment.ARG_CONVERSATION_ID to it.key
                    )
                )
            },
            onDelete = viewModel::delete
        )
        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyList.adapter = listAdapter

        viewModel.items.observe(viewLifecycleOwner) { items ->
            allItems = items
            filter(binding.searchInput.text?.toString().orEmpty())
        }
        binding.searchInput.doAfterTextChanged { filter(it?.toString().orEmpty()) }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.delete(listAdapter.itemAt(viewHolder.bindingAdapterPosition))
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                viewHolder.itemView.translationX = dX
                viewHolder.itemView.alpha = 1f - abs(dX) / recyclerView.width
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }).attachToRecyclerView(binding.historyList)
    }

    private fun filter(query: String) {
        val shown = if (query.isBlank()) {
            allItems
        } else {
            allItems.filter {
                it.question.contains(query, ignoreCase = true) ||
                    it.answer.contains(query, ignoreCase = true)
            }
        }
        listAdapter.submitList(shown)
        binding.emptyText.isVisible = shown.isEmpty()
        binding.historyList.isVisible = shown.isNotEmpty()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
