package com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks

import com.practicum.playlistmaker.mediaLibrary.domain.TracksDbRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeaturedTracksInteractorImpl(
    private val playlistRepository: FeaturedTracksRepository,
    private val trackRepository: TracksDbRepository
) : FeaturedTracksInteractor {
    override suspend fun addTrack(track: Track) {
        trackRepository.saveTrack(track)
        playlistRepository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        playlistRepository.deleteTrack(track)
        trackRepository.deleteTrackIfUnused(track.trackId)
    }

    override fun getFeaturedTracks(): Flow<List<Track>> {
        return playlistRepository.getFeaturedTracks().map { tracks ->
            tracks
                .map { track -> track.copy(isFavourite = true)}
        }
    }
}