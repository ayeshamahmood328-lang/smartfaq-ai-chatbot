package com.example.smartfaqai.util

import android.content.Context

object Prefs {
    private const val NAME = "smartfaq_preferences"
    private const val KEY_ONBOARDED = "onboarding_complete"
    private const val KEY_DARK_MODE = "dark_mode"

    private fun prefs(context: Context) =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isOnboarded(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ONBOARDED, false)

    fun setOnboarded(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_ONBOARDED, value).apply()
    }

    fun isDarkMode(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, value).apply()
    }
}
