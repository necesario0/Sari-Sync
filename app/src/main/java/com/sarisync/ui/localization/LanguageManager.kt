package com.sarisync.ui.localization

import android.content.Context
import android.content.SharedPreferences
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
 * Supported app appearance modes.
 */
enum class AppThemeMode(val code: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system")
}

/**
 * Singleton that holds all user preferences and persists them
 * via SharedPreferences so choices survive app restarts.
 *
 * Includes: language, onboarding, business name, currency symbol,
 * theme mode, notification toggles.
 *
 * NOTE: Explicit setter methods are named "updateXxx" to avoid
 * JVM signature clashes with Kotlin-generated property setters.
 */
object LanguageManager {

    private const val PREFS_NAME = "sari_sync_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_BUSINESS_NAME = "business_name"
    private const val KEY_CURRENCY_SYMBOL = "currency_symbol"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_NOTIF_LOW_STOCK = "notif_low_stock"
    private const val KEY_NOTIF_SALES = "notif_sales"

    // ── Observable state consumed by Compose ───────────────

    /** Current language. */
    var currentLanguage: Language by mutableStateOf(Language.FILIPINO)
        private set

    /** Whether the user has completed the welcome screen. */
    var onboardingDone: Boolean by mutableStateOf(false)
        private set

    /** User's business / store name. */
    var businessName: String by mutableStateOf("")
        private set

    /** Currency symbol (default ₱). */
    var currencySymbol: String by mutableStateOf("₱")
        private set

    /** App appearance mode. */
    var themeMode: AppThemeMode by mutableStateOf(AppThemeMode.SYSTEM)
        private set

    /** Low-stock notification toggle. */
    var notifLowStock: Boolean by mutableStateOf(true)
        private set

    /** Sales update notification toggle. */
    var notifSales: Boolean by mutableStateOf(false)
        private set

    private lateinit var prefs: SharedPreferences

    /** Call once from [MainActivity.onCreate] before setContent. */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val savedCode = prefs.getString(KEY_LANGUAGE, Language.FILIPINO.code)
        currentLanguage = Language.entries.firstOrNull { it.code == savedCode } ?: Language.FILIPINO
        onboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        businessName = prefs.getString(KEY_BUSINESS_NAME, "") ?: ""
        currencySymbol = prefs.getString(KEY_CURRENCY_SYMBOL, "₱") ?: "₱"

        val themeCode = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.code)
        themeMode = AppThemeMode.entries.firstOrNull { it.code == themeCode } ?: AppThemeMode.SYSTEM

        notifLowStock = prefs.getBoolean(KEY_NOTIF_LOW_STOCK, true)
        notifSales = prefs.getBoolean(KEY_NOTIF_SALES, false)
    }

    // ── Setters (each persists immediately) ────────────────
    // Named "updateXxx" to avoid JVM clash with Kotlin property setters.

    fun setLanguage(language: Language) {
        currentLanguage = language
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun completeOnboarding() {
        onboardingDone = true
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }

    fun updateBusinessName(name: String) {
        businessName = name
        prefs.edit().putString(KEY_BUSINESS_NAME, name).apply()
    }

    fun updateCurrencySymbol(symbol: String) {
        currencySymbol = symbol
        prefs.edit().putString(KEY_CURRENCY_SYMBOL, symbol).apply()
    }

    fun updateThemeMode(mode: AppThemeMode) {
        themeMode = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.code).apply()
    }

    fun updateNotifLowStock(enabled: Boolean) {
        notifLowStock = enabled
        prefs.edit().putBoolean(KEY_NOTIF_LOW_STOCK, enabled).apply()
    }

    fun updateNotifSales(enabled: Boolean) {
        notifSales = enabled
        prefs.edit().putBoolean(KEY_NOTIF_SALES, enabled).apply()
    }

    /**
     * Resets the onboarding flag so the welcome screen shows again.
     * Useful for testing or after a data wipe.
     */
    fun resetOnboarding() {
        onboardingDone = false
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, false).apply()
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
