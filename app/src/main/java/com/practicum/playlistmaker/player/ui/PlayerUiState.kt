package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

data class PlayerUiState(
    val playerState: PlayerState,
    val isBottomSheetExpanded: Boolean = false,
    val playlists: List<Playlist> = emptyList()
)
