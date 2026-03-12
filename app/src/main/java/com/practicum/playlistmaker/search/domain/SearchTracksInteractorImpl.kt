package com.practicum.playlistmaker.search.domain

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(
    private val repository: SearchTracksRepository,
    private val executor: ExecutorService
):
    SearchTracksInteractor {

    override fun searchTracks(expression: String, consumer: SearchTracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}