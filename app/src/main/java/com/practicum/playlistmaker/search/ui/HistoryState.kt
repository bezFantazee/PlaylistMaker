package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface HistoryState {
    object Empty: HistoryState
    object Cleared: HistoryState
    data class Content(
        val data: List<Track>
    ) : HistoryState
}