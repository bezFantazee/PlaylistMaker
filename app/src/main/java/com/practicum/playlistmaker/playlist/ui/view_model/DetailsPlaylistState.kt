package com.practicum.playlistmaker.playlist.ui.view_model

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface DetailsPlaylistState {
    object Empty : DetailsPlaylistState

    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>?,
        val tracksFullTime: String?,

        val isMenuExpanded: Boolean = false
    ) : DetailsPlaylistState
}
