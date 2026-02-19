package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.SearchResult

interface SearchTracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface  TracksConsumer {
        fun consume(result: SearchResult)
    }
}