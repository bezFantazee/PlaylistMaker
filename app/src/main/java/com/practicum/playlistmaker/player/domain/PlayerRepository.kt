package com.practicum.playlistmaker.player.domain

interface PlayerRepository {
    fun pausePlayer()
    fun startPlayer()
    fun preparePlayer(url: String)
    fun getCurrentTime(): Int
    fun isPlaying(): Boolean
    fun resetMediaPlayer()
}