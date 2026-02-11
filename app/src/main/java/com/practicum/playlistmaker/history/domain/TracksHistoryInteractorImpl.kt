package com.practicum.playlistmaker.history.domain

import com.practicum.playlistmaker.search.domain.models.Track

class TracksHistoryInteractorImpl(
    private val repository: TracksHistoryRepository
): TracksHistoryInteractor {
    override fun clearSavedTracks() {
        repository.clearSavedTracks()
    }

    override fun getTracks(consumer: TracksHistoryInteractor.TracksHistoryConsumer) {
        consumer.consume(repository.getTracks())
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

}