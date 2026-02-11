package com.practicum.playlistmaker.history.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksHistoryRepository {
    fun saveTrack(track: Track)
    fun getTracks(): MutableList<Track>
    fun clearSavedTracks()
}