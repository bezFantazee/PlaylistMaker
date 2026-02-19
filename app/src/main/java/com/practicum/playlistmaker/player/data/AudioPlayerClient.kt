package com.practicum.playlistmaker.player.data

interface AudioPlayerClient {
    fun play()
    fun pause()
    fun prepare(url: String)
    fun getTime(): Int
    fun resetMediaPlayer()
}