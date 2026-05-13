package com.practicum.playlistmaker.player.ui

import androidx.annotation.StringRes

sealed interface PlayerEvent {
    data class ShowToast(
        @StringRes val messageRes: Int,
        val args: List<Any> = emptyList()
    ) : PlayerEvent
}