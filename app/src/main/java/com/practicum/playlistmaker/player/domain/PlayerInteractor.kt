package com.practicum.playlistmaker.player.domain

interface PlayerInteractor {
    fun pausePlayer()
    fun startPlayer()
    fun preparePlayer(url: String?)
    fun getCurrentTime(): Int

    fun isPlaying(): Boolean
    fun onCleared()
}