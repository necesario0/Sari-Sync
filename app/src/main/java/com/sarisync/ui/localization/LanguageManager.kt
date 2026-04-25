package com.sarisync.ui.localization

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Supported languages.
 */
enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    FILIPINO("fil", "Filipino")
}

/**
 * Singleton that holds the current language selection and persists it
 * via SharedPreferences so the choice survives app restarts.
 */
object LanguageManager {

    private const val PREFS_NAME = "sari_sync_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    /** Observable state consumed by Compose. */
    var currentLanguage: Language by mutableStateOf(Language.FILIPINO)
        private set

    /** Whether the user has completed the welcome / onboarding screen. */
    var onboardingDone: Boolean by mutableStateOf(false)
        private set

    private lateinit var prefs: SharedPreferences

    /** Call once from [MainActivity.onCreate] before setContent. */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCode = prefs.getString(KEY_LANGUAGE, Language.FILIPINO.code)
        currentLanguage = Language.entries.firstOrNull { it.code == savedCode } ?: Language.FILIPINO
        onboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
    }

    fun setLanguage(language: Language) {
        currentLanguage = language
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun completeOnboarding() {
        onboardingDone = true
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }
}

/** Convenience: returns the [AppStrings] for the current language. */
val currentStrings: AppStrings
    get() = when (LanguageManager.currentLanguage) {
        Language.ENGLISH -> EnglishStrings
        Language.FILIPINO -> FilipinoStrings
    }

/**
 * CompositionLocal so any composable can access strings via
 * `LocalStrings.current` without passing them through parameters.
 */
val LocalStrings = compositionLocalOf { FilipinoStrings }
