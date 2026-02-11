package com.practicum.playlistmaker.history.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksHistoryInteractor {
    fun saveTrack(track: Track)
    fun getTracks(consumer: TracksHistoryConsumer)
    fun clearSavedTracks()

    interface TracksHistoryConsumer {
        fun consume(searchHistory: List<Track>)
    }
}