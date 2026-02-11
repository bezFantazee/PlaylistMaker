package com.practicum.playlistmaker.player.domain

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun preparePlayer(url: String?) {
        if(url == null) {
            return
        }
        repository.preparePlayer(url)
    }
}