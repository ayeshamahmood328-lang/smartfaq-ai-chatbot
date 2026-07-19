package com.example.smartfaqai.ui.category

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
import com.example.smartfaqai.databinding.FragmentCategoryBinding
import com.example.smartfaqai.ui.QuestionAdapter
import com.example.smartfaqai.ui.ViewModelFactory

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by viewModels {
        ViewModelFactory.from(requireActivity().application as SmartFaqApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val category = arguments?.getString(ARG_CATEGORY).orEmpty()
        val info = CategoryInfo.forName(category)
        val adapter = QuestionAdapter { faq ->
            findNavController().navigate(
                R.id.action_categoryFragment_to_faqDetailFragment,
                bundleOf(ARG_FAQ_ID to faq.id)
            )
        }

        binding.toolbar.title = info.name
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.categoryDescription.text = info.description
        binding.faqList.layoutManager = LinearLayoutManager(requireContext())
        binding.faqList.adapter = adapter
        binding.searchInput.doAfterTextChanged { viewModel.search(it?.toString().orEmpty()) }

        viewModel.faqs.observe(viewLifecycleOwner) { faqs ->
            adapter.submitList(faqs)
            binding.questionCount.text = getString(R.string.category_questions, faqs.size)
            binding.emptyText.isVisible = faqs.isEmpty()
            binding.faqList.isVisible = faqs.isNotEmpty()
        }
        viewModel.loadCategory(info.name)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_CATEGORY = "category"
        const val ARG_FAQ_ID = "faqId"
    }
}
