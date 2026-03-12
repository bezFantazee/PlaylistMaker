package com.practicum.playlistmaker.settings.domain

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.model.ThemeState

class ThemeInteractorImpl(
    private val repository: ThemeRepository,
    private val themeManager: ThemeManager
): ThemeInteractor {
    override fun saveTheme(themeName: ThemeState?) {
        if(themeName == null) return
        repository.saveTheme(themeName)
        themeManager.applyTheme(themeName)
    }

    override fun getCurrentTheme(): ThemeState {
        val currentTheme = repository.getCurrentTheme()
        return if (currentTheme != null) {
            currentTheme
        } else {
            // При первом запуске определяем системную тему
            val systemTheme = themeManager.detectSystemTheme()
            saveTheme(systemTheme)
            systemTheme
        }
    }
}