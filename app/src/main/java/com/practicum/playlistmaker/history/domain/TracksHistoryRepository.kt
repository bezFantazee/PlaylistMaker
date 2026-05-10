package com.practicum.playlistmaker.history.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksHistoryRepository {
    fun saveTrack(track: Track)
    suspend fun getTracks(): Flow<List<Track>>
    fun clearSavedTracks()
}