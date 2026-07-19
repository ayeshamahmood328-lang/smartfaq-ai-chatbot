package com.example.smartfaqai.ui.favorites

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartfaqai.R
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.databinding.FragmentFavoritesBinding
import com.example.smartfaqai.ui.SavedAnswerAdapter
import com.example.smartfaqai.ui.SavedAnswerItem
import com.example.smartfaqai.ui.ViewModelFactory
import com.example.smartfaqai.ui.chat.ChatFragment

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels {
        ViewModelFactory.from(requireActivity().application as SmartFaqApp)
    }

    private lateinit var listAdapter: SavedAnswerAdapter
    private var allItems = emptyList<SavedAnswerItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listAdapter = SavedAnswerAdapter(
            onOpen = {
                findNavController().navigate(
                    R.id.chatFragment,
                    bundleOf(
                        ChatFragment.ARG_QUESTION to it.question,
                        ChatFragment.ARG_CATEGORY to it.category
                    )
                )
            },
            onDelete = viewModel::delete
        )
        binding.favoritesList.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesList.adapter = listAdapter

        viewModel.items.observe(viewLifecycleOwner) { items ->
            allItems = items
            filter(binding.searchInput.text?.toString().orEmpty())
        }
        binding.searchInput.doAfterTextChanged { filter(it?.toString().orEmpty()) }
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
        binding.favoritesList.isVisible = shown.isNotEmpty()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
