package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val trackUrl: String?) : ViewModel() {
    //LiveData
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData
    private val timeLiveData = MutableLiveData<String>(formatTime(0))
    fun observeTime(): LiveData<String> = timeLiveData

    //Interactors
    private val playerInteractor = Creator.providePlayerInteractor(object : OnTrackCompletionListener{
        override fun OnTrackPrepared() {
            playerStateLiveData.value = PlayerState.PREPARED
        }

        override fun onTrackCompleted() {
            playerStateLiveData.value = PlayerState.PREPARED
            resetTimer()
        }
    })

    private val handler = Handler(Looper.getMainLooper())
    //private val mediaPlayer = MediaPlayer()
    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == PlayerState.PLAYING) {
            startTimerUpdate()
        }
    }

    init {
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
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PAUSE -> startPlayer()
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
        timeLiveData.value = "00:00"
    }
    private fun formatTime(mSeconds: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mSeconds)
    }

    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L

        fun getFabric(trackUrl: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }
}