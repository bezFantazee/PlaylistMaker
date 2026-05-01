package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeaturedTracksInteractorImpl(
    private val repository: FeaturedTracksRepository
) : FeaturedTracksInteractor {
    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getFeaturedTracks(): Flow<List<Track>> {
        return repository.getFeaturedTracks().map { tracks ->
            tracks
                .map { track -> track.copy(isFavourite = true)}
        }
    }
}