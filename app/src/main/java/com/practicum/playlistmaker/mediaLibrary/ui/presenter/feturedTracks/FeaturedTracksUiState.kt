package com.practicum.playlistmaker.mediaLibrary.ui.presenter.feturedTracks

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FeaturedTracksUiState {
    object Empty : FeaturedTracksUiState()
    class Content(val tracks: List<Track>) : FeaturedTracksUiState()
}