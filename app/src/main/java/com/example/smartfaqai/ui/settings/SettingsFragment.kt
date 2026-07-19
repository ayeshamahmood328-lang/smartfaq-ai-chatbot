package com.example.smartfaqai.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.smartfaqai.R
import com.example.smartfaqai.SmartFaqApp
import com.example.smartfaqai.databinding.FragmentSettingsBinding
import com.example.smartfaqai.ui.ViewModelFactory
import com.example.smartfaqai.util.Prefs
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        ViewModelFactory.from(requireActivity().application as SmartFaqApp)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.darkModeSwitch.isChecked = Prefs.isDarkMode(requireContext())
        binding.darkModeSwitch.setOnCheckedChangeListener { _, enabled ->
            Prefs.setDarkMode(requireContext(), enabled)
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.clearHistory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.clear_history)
                .setMessage("Delete all conversation history? This cannot be undone.")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete) { _, _ -> viewModel.clearHistory() }
                .show()
        }

        binding.aboutApp.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.about_message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }

        binding.privacyPolicy.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.privacy_policy)
                .setMessage(
                    "SmartFAQ AI processes FAQ searches locally on your device. " +
                        "Your conversations stay in the app database and are never uploaded."
                )
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }

        binding.rateApp.setOnClickListener {
            val packageName = requireContext().packageName
            val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            val web = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            runCatching { startActivity(market) }.onFailure { startActivity(web) }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
