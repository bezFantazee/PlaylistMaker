package com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed class PlaylistsUiState {
    object Empty : PlaylistsUiState()
    class Content(val playlists: List<Playlist>) : PlaylistsUiState()
}