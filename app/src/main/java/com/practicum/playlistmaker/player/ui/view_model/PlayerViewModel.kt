package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.TrackCompletionListenerHolder
import com.practicum.playlistmaker.player.ui.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackUrl: String?,
    private val playerInteractor: PlayerInteractor,
    private var listenerHolder: TrackCompletionListenerHolder
) : ViewModel() {
    //LiveData
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerJob: Job? = null

    init {
        listenerHolder.delegate = object : OnTrackCompletionListener {
            override fun OnTrackPrepared() {
                playerStateLiveData.value = PlayerState.Prepared
            }

            override fun onTrackCompleted() {
                pauseTimer()
                playerStateLiveData.value = PlayerState.Completed
            }
        }
        preparePlayer()
    }

    //состояния
    override fun onCleared() {
        super.onCleared()
        playerInteractor.onCleared()
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
            is PlayerState.Prepared -> startPlayer()
            is PlayerState.Completed -> {
                preparePlayer()
            }
            else -> {}
        }
    }

    //функции для управления медиаплеером
    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.value = PlayerState.Playing(
            getCurrentPlayerPosition()
        )
        startTimerUpdate()
    }
    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        pauseTimer()
        playerStateLiveData.value = PlayerState.Paused(
            getCurrentPlayerPosition()
        )
    }

    private fun preparePlayer() {
        if(trackUrl.isNullOrEmpty()){
            return
        }
        playerInteractor.preparePlayer(trackUrl)
    }

    //устанока времени таймера воспроизведения
    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while(playerInteractor.isPlaying()) {
                delay(UPDATE_TIMETRACK_TIME)
                playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }
    private fun pauseTimer() {
        timerJob?.cancel()
    }
    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentTime()) ?: "00:00"
    }

    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L
    }
}