package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRemoteRepository
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(private val repository: TracksRemoteRepository): SearchTracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchTracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}