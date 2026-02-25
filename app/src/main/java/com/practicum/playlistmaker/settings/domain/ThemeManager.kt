package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.domain.model.ThemeState

interface ThemeManager {
    fun detectSystemTheme(): ThemeState
    fun applyTheme(theme: ThemeState)
}