package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksDbRepository {
    suspend fun deleteTrackIfUnused(trackId: Int)
    suspend fun saveTrack(track: Track)
}