package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading: SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState

    data class Error(
        val errorMessage: String,
        val extraMessage: String
    ) : SearchState
}