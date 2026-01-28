package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.preferences.SharedPreferenceClient
import com.practicum.playlistmaker.domain.preferences.ThemeRepository

class ThemeRepositoryImpl(private val sharedPreferenceClient: SharedPreferenceClient): ThemeRepository {
    override fun saveTheme(themeName: String, key: String) {
        sharedPreferenceClient.save(themeName, key)
    }

    override fun getCurrentTheme(key: String): String? {
        return sharedPreferenceClient.get(key)
    }

}