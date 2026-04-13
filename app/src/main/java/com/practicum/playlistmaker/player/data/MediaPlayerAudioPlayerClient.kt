package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener

class MediaPlayerAudioPlayerClient(
    private val completionListener: OnTrackCompletionListener,
    private val mediaPlayer: MediaPlayer
) : AudioPlayerClient {

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun prepare(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            completionListener.OnTrackPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            completionListener.onTrackCompleted()
        }
    }

    override fun getTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun resetMediaPlayer() {
        mediaPlayer.reset()
    }
}