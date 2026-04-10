package com.practicum.playlistmaker.player.ui

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val currentTime: String) {
    class Default : PlayerState(false, "00:00")
    object Prepared : PlayerState(true, "00:00")
    class Playing(currentTime: String) : PlayerState(true, currentTime)
    class Paused(currentTime: String) : PlayerState(true, currentTime)
    object Completed : PlayerState(true, "00:00")
}
