package com.practicum.playlistmaker.playlist.ui.view_model

import androidx.annotation.StringRes

sealed interface DetailPlaylistEvent {
    data class ShowToast(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList()
    ) : DetailPlaylistEvent
}