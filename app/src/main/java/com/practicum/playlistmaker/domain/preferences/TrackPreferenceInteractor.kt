package com.practicum.playlistmaker.domain.preferences

import com.practicum.playlistmaker.domain.models.Track

interface TrackPreferenceInteractor {
    fun saveTrack(track: Track, key: String)
    fun getTracks(key: String): MutableList<Track>
    fun clearSavedTracks()
}