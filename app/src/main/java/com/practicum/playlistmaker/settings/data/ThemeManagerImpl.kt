package com.practicum.playlistmaker.settings.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.ThemeManager
import com.practicum.playlistmaker.settings.domain.model.ThemeState

class ThemeManagerImpl(
    private val context: Context
) : ThemeManager {
    override fun detectSystemTheme(): ThemeState {
        val nightModeFlags = context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            ThemeState.DARK_THEME
        } else {
            ThemeState.LIGHT_THEME
        }
    }

    override fun applyTheme(theme: ThemeState) {
        val mode = when(theme) {
            ThemeState.DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeState.LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}