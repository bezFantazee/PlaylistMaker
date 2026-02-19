package com.practicum.playlistmaker.search.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class SearchResult {
    data class Success(val tracks: List<Track>): SearchResult()
    object NetWorkError: SearchResult()
    object NoResults: SearchResult()
}