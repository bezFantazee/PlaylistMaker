package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.SearchResult

interface SearchTracksRepository {
    fun searchTracks(expression: String): SearchResult
}