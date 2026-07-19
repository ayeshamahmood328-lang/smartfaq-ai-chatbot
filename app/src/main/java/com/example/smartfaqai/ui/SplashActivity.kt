package com.example.smartfaqai.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.smartfaqai.databinding.ActivitySplashBinding
import com.example.smartfaqai.util.Prefs

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoCard.alpha = 0f
        binding.logoCard.scaleX = 0.75f
        binding.logoCard.scaleY = 0.75f
        binding.logoCard.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(700)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (Prefs.isOnboarded(this)) {
                MainActivity::class.java
            } else {
                OnboardingActivity::class.java
            }
            startActivity(Intent(this, next))
            finish()
        }, 2000)
    }
}
