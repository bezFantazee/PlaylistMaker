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
import com.practicum.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val trackUrl: String?) : ViewModel() {

    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L

        fun getFabric(trackUrl: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.DEFAULT)
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData
    private val timeLiveData = MutableLiveData<String>("00:00")
    fun observeTime(): LiveData<String> = timeLiveData

    private val handler = Handler(Looper.getMainLooper())
    private val mediaPlayer = MediaPlayer()
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
        mediaPlayer.release()
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
        mediaPlayer.start()
        playerStateLiveData.value = PlayerState.PLAYING
        startTimerUpdate()
    }
    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.value = PlayerState.PAUSE
    }

    private fun preparePlayer() {
        if(trackUrl.isNullOrEmpty()){
            return
        }
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.value = PlayerState.PREPARED
            resetTimer()
        }
    }

    //устанока времени таймера воспроизведения
    private fun startTimerUpdate() {
        timeLiveData.value = formatTime(mediaPlayer.currentPosition)
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
}