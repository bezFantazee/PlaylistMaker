package com.practicum.playlistmaker.history.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksHistoryInteractorImpl(
    private val repository: TracksHistoryRepository
): TracksHistoryInteractor {
    override fun clearSavedTracks() {
        repository.clearSavedTracks()
    }

    override suspend fun getTracks(): Flow<List<Track>> {
        return repository.getTracks()
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

}