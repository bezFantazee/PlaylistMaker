package com.practicum.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.preferences.ThemeInteractor
import com.practicum.playlistmaker.domain.preferences.ThemeRepository
import com.practicum.playlistmaker.ui.settings.SettingsActivity.Companion.DARK_THEME

class ThemeInteractorImpl(private val repository: ThemeRepository): ThemeInteractor {
    override fun saveTheme(themeName: String, key: String) {
        repository.saveTheme(themeName, key)
        switchTheme(themeName == DARK_THEME)
    }

    override fun getCurrentTheme(key: String): String? {
        return repository.getCurrentTheme(key)
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            } else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}