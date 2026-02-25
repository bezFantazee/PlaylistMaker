package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.TrackCompletionListenerHolder
import com.practicum.playlistmaker.player.ui.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackUrl: String?,
    private val playerInteractor: PlayerInteractor,
    private var listenerHolder: TrackCompletionListenerHolder
) : ViewModel() {
    //LiveData
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData
//    private val timeLiveData = MutableLiveData<String>(formatTime(0))
//    fun observeTime(): LiveData<String> = timeLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerStateLiveData.value is PlayerState.Playing) {
            startTimerUpdate()
        }
    }

    init {
        listenerHolder.delegate = object : OnTrackCompletionListener {
            override fun OnTrackPrepared() {
                playerStateLiveData.value = PlayerState.Prepared
            }

            override fun onTrackCompleted() {
                playerStateLiveData.value = PlayerState.Prepared
                resetTimer()
            }
        }
        preparePlayer()
    }

    //состояния
    override fun onCleared() {
        super.onCleared()
        playerInteractor.onCleared()
        resetTimer()
    }

    fun onPause() {
        pausePlayer()
    }
    fun onPlayButtonClicked() {
        when(playerStateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Paused -> {
                startPlayer()
            }
            else -> startPlayer()
        }
    }

    //функции для управления медиаплеером
    private fun startPlayer() {
        playerInteractor.startPlayer()
        startTimerUpdate()
        playerStateLiveData.value = PlayerState.Playing(
            formatTime(playerInteractor.getCurrentTime())
        )
    }
    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        playerStateLiveData.value = PlayerState.Paused(
            formatTime(playerInteractor.getCurrentTime())
        )
    }

    private fun preparePlayer() {
        if(trackUrl.isNullOrEmpty()){
            return
        }
        playerInteractor.preparePlayer(trackUrl)
        playerStateLiveData.value = PlayerState.Prepared
    }

    //устанока времени таймера воспроизведения
    private fun startTimerUpdate() {
        val currentTime = playerInteractor.getCurrentTime()
        playerStateLiveData.value = PlayerState.Playing(formatTime(currentTime))
        handler.postDelayed(timerRunnable, UPDATE_TIMETRACK_TIME)
    }
    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }
    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        playerStateLiveData.value = PlayerState.Paused(
            formatTime(0)
        )
    }
    private fun formatTime(mSeconds: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mSeconds)
    }

    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L
    }
}