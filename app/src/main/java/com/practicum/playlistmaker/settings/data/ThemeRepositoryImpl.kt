package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.history.data.StorageClient
import com.practicum.playlistmaker.settings.domain.ThemeRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeState

class ThemeRepositoryImpl(private val prefsStorageClient: StorageClient<String>):
    ThemeRepository {
    override fun saveTheme(themeName: ThemeState) {
        prefsStorageClient.save(themeName.name)
    }

    override fun getCurrentTheme(): ThemeState? {
        val stringState = prefsStorageClient.get()
        return if (stringState != null) {
            ThemeState.valueOf(stringState)
        } else {
            null
        }
    }
}