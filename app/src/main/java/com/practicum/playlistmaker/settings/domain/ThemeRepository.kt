package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.domain.model.ThemeState

interface ThemeRepository {
    fun saveTheme(themeName: ThemeState)
    fun getCurrentTheme(): ThemeState?
}