package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.domain.model.ThemeState

interface ThemeInteractor {
    fun saveTheme(themeName: ThemeState?)
    fun getCurrentTheme(): ThemeState
}