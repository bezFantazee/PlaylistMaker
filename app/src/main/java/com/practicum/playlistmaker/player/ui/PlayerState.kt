package com.practicum.playlistmaker.player.ui

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    open val currentTime: String,
    open val isFavourite: Boolean,
) {
    data class Default(
        override val currentTime: String = "00:00",
        override val isFavourite: Boolean = false
    ) : PlayerState(false, false, currentTime, isFavourite)
    data class Prepared(
        override val currentTime: String = "00:00",
        override val isFavourite: Boolean = false
    ) : PlayerState(true, false, currentTime, isFavourite)
    data class Playing(
        override val currentTime: String,
        override val isFavourite: Boolean
        ) : PlayerState(true, true, currentTime, isFavourite)
    data class Paused(
        override val currentTime: String,
        override val isFavourite: Boolean
    ) : PlayerState(true, false, currentTime, isFavourite)
    data class Completed(
        override val currentTime: String = "00:00",
        override val isFavourite: Boolean
    ) : PlayerState(true, false,currentTime, isFavourite)
}
