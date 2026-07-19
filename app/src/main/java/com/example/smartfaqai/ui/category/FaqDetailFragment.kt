package com.example.smartfaqai.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartfaqai.R
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.databinding.FragmentFaqDetailBinding
import com.example.smartfaqai.ui.ViewModelFactory

class FaqDetailFragment : Fragment() {
    private var _binding: FragmentFaqDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FaqDetailViewModel by viewModels {
        ViewModelFactory.from(requireActivity().application as SmartFaqApp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.favoriteButton.isEnabled = false
        binding.favoriteButton.setOnClickListener {
            viewModel.favorite()
            Toast.makeText(requireContext(), R.string.favorite_saved, Toast.LENGTH_SHORT).show()
        }

        viewModel.faq.observe(viewLifecycleOwner) { faq ->
            faq ?: return@observe
            binding.categoryChip.text = faq.category
            binding.questionText.text = faq.question
            binding.answerText.text = faq.answer
            binding.favoriteButton.isEnabled = true
        }

        val faqId = arguments?.getLong(CategoryFragment.ARG_FAQ_ID, -1L) ?: -1L
        if (faqId > 0) viewModel.load(faqId)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
