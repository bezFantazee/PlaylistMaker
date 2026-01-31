package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto

interface PreferencesClient {
    fun save(json: String, key: String)
    fun get(key: String): String?
    fun clear()
}