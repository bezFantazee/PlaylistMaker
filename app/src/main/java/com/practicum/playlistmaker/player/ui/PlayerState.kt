package com.practicum.playlistmaker.player.ui

sealed interface PlayerState {
    object Default : PlayerState
    object Prepared : PlayerState
    data class Playing(val currentTime: String) : PlayerState
    data class Paused(val currentTime: String) : PlayerState
}