package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SearchTracksInteractorImpl(
    private val repository: SearchTracksRepository
):
    SearchTracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchResult> {
        return repository.searchTracks(expression)
    }
}