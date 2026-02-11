package com.practicum.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.model.ThemeState
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class ThemeInteractorImpl(private val repository: ThemeRepository): ThemeInteractor {
    override fun saveTheme(themeName: ThemeState?) {
        if(themeName == null) return
        repository.saveTheme(themeName)
    }

    override fun getCurrentTheme(): ThemeState {
        val currentTheme = repository.getCurrentTheme() ?: ThemeState.LIGHT_THEME
        return currentTheme
    }
}