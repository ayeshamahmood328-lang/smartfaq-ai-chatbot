package com.example.smartfaqai.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.smartfaqai.R
import com.example.smartfaqai.databinding.ActivityOnboardingBinding
import com.example.smartfaqai.databinding.ItemOnboardingBinding
import com.example.smartfaqai.util.Prefs

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    private val pages = listOf(
        OnboardingPage(
            R.drawable.ic_smart_ai,
            "AI Powered FAQ Assistant",
            "Get reliable answers from an intelligent offline knowledge base."
        ),
        OnboardingPage(
            R.drawable.ic_search_smart,
            "Find Answers Instantly",
            "Search more than 100 useful questions across everyday categories."
        ),
        OnboardingPage(
            R.drawable.ic_time_smart,
            "Save Time with Smart Search",
            "Keep your history and favorite answers ready whenever you need them."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = OnboardingAdapter(pages)
        binding.skipButton.setOnClickListener { finishOnboarding() }
        binding.nextButton.setOnClickListener {
            if (binding.viewPager.currentItem == pages.lastIndex) {
                finishOnboarding()
            } else {
                binding.viewPager.currentItem += 1
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.nextButton.text =
                    if (position == pages.lastIndex) getString(R.string.get_started)
                    else getString(R.string.next)
                binding.dots.text = pages.indices.joinToString("  ") {
                    if (it == position) "●" else "○"
                }
            }
        })
    }

    private fun finishOnboarding() {
        Prefs.setOnboarded(this, true)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

data class OnboardingPage(val icon: Int, val title: String, val description: String)

private class OnboardingAdapter(private val pages: List<OnboardingPage>) :
    RecyclerView.Adapter<OnboardingAdapter.PageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PageHolder(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = pages.size

    override fun onBindViewHolder(holder: PageHolder, position: Int) = holder.bind(pages[position])

    class PageHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnboardingPage) {
            binding.pageIcon.setImageResource(page.icon)
            binding.pageTitle.text = page.title
            binding.pageDescription.text = page.description
        }
    }
}
