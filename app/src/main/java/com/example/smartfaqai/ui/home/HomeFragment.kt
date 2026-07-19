package com.example.smartfaqai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartfaqai.R
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.databinding.FragmentHomeBinding
import com.example.smartfaqai.ui.QuestionAdapter
import com.example.smartfaqai.ui.SavedAnswerAdapter
import com.example.smartfaqai.ui.ViewModelFactory
import com.example.smartfaqai.ui.chat.ChatFragment
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.from(requireActivity().application as SmartFaqApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val suggestedAdapter = QuestionAdapter { openChat(it.question, category = it.category) }
        val recentAdapter = SavedAnswerAdapter(
            { openChat(it.question, conversationId = it.key) },
            {}
        )

        binding.suggestedList.layoutManager = LinearLayoutManager(requireContext())
        binding.suggestedList.adapter = suggestedAdapter
        binding.recentList.layoutManager = LinearLayoutManager(requireContext())
        binding.recentList.adapter = recentAdapter

        viewModel.suggested.observe(viewLifecycleOwner, suggestedAdapter::submitList)
        viewModel.recent.observe(viewLifecycleOwner) { items ->
            recentAdapter.submitList(items)
            binding.emptyRecent.isVisible = items.isEmpty()
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchInput.text?.toString()?.takeIf { it.isNotBlank() }?.let(::openChat)
                true
            } else {
                false
            }
        }

        for (index in 0 until binding.categoryGroup.childCount) {
            val chip = binding.categoryGroup.getChildAt(index) as? Chip ?: continue
            chip.setOnClickListener {
                findNavController().navigate(
                    R.id.action_homeFragment_to_categoryFragment,
                    bundleOf("category" to chip.text.toString())
                )
            }
        }
    }

    private fun openChat(
        question: String,
        category: String = "",
        conversationId: String = ""
    ) {
        findNavController().navigate(
            R.id.action_homeFragment_to_chatFragment,
            bundleOf(
                ChatFragment.ARG_QUESTION to question,
                ChatFragment.ARG_CATEGORY to category,
                ChatFragment.ARG_CONVERSATION_ID to conversationId
            )
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
