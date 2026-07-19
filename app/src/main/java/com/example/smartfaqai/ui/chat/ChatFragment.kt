package com.example.smartfaqai.ui.chat

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartfaqai.R
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.databinding.FragmentChatBinding
import com.example.smartfaqai.ui.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val conversationId: String by lazy {
        arguments?.getString(ARG_CONVERSATION_ID)
            ?.takeIf { it.isNotBlank() }
            ?: ChatConversation.idForCategory(arguments?.getString(ARG_CATEGORY).orEmpty())
    }

    private val viewModel: ChatViewModel by viewModels {
        ViewModelFactory.from(
            requireActivity().application as SmartFaqApp,
            conversationId
        )
    }

    private lateinit var chatAdapter: ChatAdapter

    private val voiceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spoken = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                if (!spoken.isNullOrBlank()) {
                    binding.messageInput.setText(spoken)
                    binding.messageInput.setSelection(spoken.length)
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chatAdapter = ChatAdapter(::copyAnswer, ::shareAnswer) { message ->
            viewModel.favorite(message)
            Toast.makeText(requireContext(), "Saved to favorites", Toast.LENGTH_SHORT).show()
        }

        binding.messageList.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.messageList.adapter = chatAdapter

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.sendButton.setOnClickListener { send() }
        binding.clearButton.setOnClickListener { confirmClear() }
        binding.micButton.setOnClickListener { launchVoiceInput() }
        binding.messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                send()
                true
            } else {
                false
            }
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                if (messages.isNotEmpty()) {
                    binding.messageList.scrollToPosition(messages.lastIndex)
                }
            }
        }
        viewModel.typing.observe(viewLifecycleOwner) { binding.typingText.isVisible = it }

        val question = arguments?.getString(ARG_QUESTION).orEmpty()
        val isReopeningConversation =
            !arguments?.getString(ARG_CONVERSATION_ID).isNullOrBlank()
        if (question.isNotBlank() && savedInstanceState == null && !isReopeningConversation) {
            viewModel.sendInitialQuestion(question, getString(R.string.no_answer))
        }
    }

    private fun send() {
        val text = binding.messageInput.text?.toString().orEmpty()
        if (text.isBlank()) return
        binding.messageInput.text?.clear()
        viewModel.send(text, getString(R.string.no_answer))
    }

    private fun confirmClear() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.clear_chat)
            .setMessage(R.string.clear_chat_confirmation)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.clear_chat) { _, _ -> viewModel.clear() }
            .show()
    }

    private fun launchVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask SmartFAQ AI")
        }
        runCatching { voiceLauncher.launch(intent) }
            .onFailure {
                Toast.makeText(requireContext(), "Voice input is not available", Toast.LENGTH_SHORT).show()
            }
    }

    private fun copyAnswer(message: UiMessage) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("SmartFAQ answer", message.text))
        Toast.makeText(requireContext(), "Answer copied", Toast.LENGTH_SHORT).show()
    }

    private fun shareAnswer(message: UiMessage) {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message.text)
                },
                "Share answer"
            )
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_QUESTION = "question"
        const val ARG_CATEGORY = "category"
        const val ARG_CONVERSATION_ID = "conversationId"
    }
}
