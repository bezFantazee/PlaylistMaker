package com.practicum.playlistmaker.domain.preferences

interface ThemeInteractor {
    fun saveTheme(themeName: String, key: String)
    fun getCurrentTheme(key: String): String?
}