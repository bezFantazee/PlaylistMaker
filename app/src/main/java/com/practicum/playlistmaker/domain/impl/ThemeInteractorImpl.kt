package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.data.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.preferences.ThemeInteractor
import com.practicum.playlistmaker.domain.preferences.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository): ThemeInteractor {
    override fun saveTheme(themeName: String, key: String) {
        repository.saveTheme(themeName, key)
    }

    override fun getCurrentTheme(key: String): String? {
        return repository.getCurrentTheme(key)
    }
}