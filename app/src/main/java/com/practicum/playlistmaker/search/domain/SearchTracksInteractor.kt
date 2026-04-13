package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun searchTracks(expression: String): Flow<SearchResult>
}