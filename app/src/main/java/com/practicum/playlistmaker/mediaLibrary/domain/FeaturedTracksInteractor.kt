package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FeaturedTracksInteractor {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFeaturedTracks(): Flow<List<Track>>
}