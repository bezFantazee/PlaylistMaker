package com.practicum.playlistmaker.player.data

import com.practicum.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(
    private val audioPlayerClient: AudioPlayerClient
) : PlayerRepository {
    override fun pausePlayer() {
        audioPlayerClient.pause()
    }

    override fun startPlayer() {
        audioPlayerClient.play()
    }

    override fun preparePlayer(url: String) {
        audioPlayerClient.prepare(url)
    }

    override fun getCurrentTime(): Int {
        return audioPlayerClient.getTime()
    }

    override fun resetMediaPlayer() {
        audioPlayerClient.resetMediaPlayer()
    }
}