package com.practicum.playlistmaker.mediaLibrary.presenter

import com.practicum.playlistmaker.search.domain.models.Track

sealed class FeaturedTracksState {
    object Empty : FeaturedTracksState()
    class Content(val tracks: List<Track>) : FeaturedTracksState()
}