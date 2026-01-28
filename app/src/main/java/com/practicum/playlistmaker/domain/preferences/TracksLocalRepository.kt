package com.practicum.playlistmaker.domain.preferences
import com.practicum.playlistmaker.domain.models.Track

interface TracksLocalRepository {
    fun saveTrack(tracks: List<Track>, key: String)
    fun getTracks(key: String): MutableList<Track>
    fun clearSavedTracks()
}