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
import com.practicum.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackUrl: String?,
    private val playerInteractor: PlayerInteractor,
    private var listenerHolder: TrackCompletionListenerHolder
) : ViewModel() {
    //LiveData
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData
    private val timeLiveData = MutableLiveData<String>(formatTime(0))
    fun observeTime(): LiveData<String> = timeLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == PlayerState.PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        listenerHolder.delegate = object : OnTrackCompletionListener {
            override fun OnTrackPrepared() {
                playerStateLiveData.value = PlayerState.PREPARED
            }

            override fun onTrackCompleted() {
                playerStateLiveData.value = PlayerState.PREPARED
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
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.PAUSE -> {
                startPlayer()
            }
            else -> startPlayer()
        }
    }

    //функции для управления медиаплеером
    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.value = PlayerState.PLAYING
        startTimerUpdate()
    }
    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        playerStateLiveData.value = PlayerState.PAUSE
    }

    private fun preparePlayer() {
        if(trackUrl.isNullOrEmpty()){
            return
        }
        playerInteractor.preparePlayer(trackUrl)
        playerStateLiveData.value = PlayerState.PREPARED
    }

    //устанока времени таймера воспроизведения
    private fun startTimerUpdate() {
        timeLiveData.value = formatTime(playerInteractor.getCurrentTime())
        handler.postDelayed(timerRunnable, UPDATE_TIMETRACK_TIME)
    }
    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }
    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        timeLiveData.value = formatTime(0)
    }
    private fun formatTime(mSeconds: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mSeconds)
    }

    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L
    }
}