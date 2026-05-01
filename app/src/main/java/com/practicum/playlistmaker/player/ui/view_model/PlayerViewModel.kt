package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksInteractor
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.TrackCompletionListenerHolder
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private var listenerHolder: TrackCompletionListenerHolder,
    private val featuredTracksInteractor: FeaturedTracksInteractor
) : ViewModel() {
    //LiveData
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default(isFavourite = track.isFavourite))
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerJob: Job? = null

    init {
        listenerHolder.delegate = object : OnTrackCompletionListener {
            override fun OnTrackPrepared() {
                playerStateLiveData.value = PlayerState.Prepared(isFavourite = track.isFavourite)
            }

            override fun onTrackCompleted() {
                pauseTimer()
                playerStateLiveData.value = PlayerState.Completed(isFavourite = track.isFavourite)
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

    fun onFavoriteClicked() {
        val newFavouriteState = !track.isFavourite
        viewModelScope.launch {
            if(track.isFavourite) {
                featuredTracksInteractor.deleteTrack(track)
            } else {
               featuredTracksInteractor.addTrack(track)
            }

            track.isFavourite = newFavouriteState

            val currentState = playerStateLiveData.value ?: return@launch
            playerStateLiveData.value = when (currentState) {
                is PlayerState.Default -> currentState.copy(isFavourite = newFavouriteState)
                is PlayerState.Prepared -> currentState.copy(isFavourite = newFavouriteState)
                is PlayerState.Playing -> currentState.copy(isFavourite = newFavouriteState)
                is PlayerState.Paused -> currentState.copy(isFavourite = newFavouriteState)
                is PlayerState.Completed -> currentState.copy(isFavourite = newFavouriteState)
            }
        }
        
    }

    //функции для управления медиаплеером
    private fun startPlayer() {
        playerInteractor.startPlayer()
        playerStateLiveData.value = PlayerState.Playing(
            currentTime = getCurrentPlayerPosition(),
            isFavourite = track.isFavourite
        )
        startTimerUpdate()
    }
    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        pauseTimer()
        playerStateLiveData.value = PlayerState.Paused(
            currentTime = getCurrentPlayerPosition(),
            isFavourite = track.isFavourite
        )
    }

    private fun preparePlayer() {
        if(track.previewUrl.isNullOrEmpty()){
            return
        }
        playerInteractor.preparePlayer(track.previewUrl)
    }

    //устанока времени таймера воспроизведения
    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while(playerInteractor.isPlaying()) {
                delay(UPDATE_TIMETRACK_TIME)
                playerStateLiveData.postValue(
                    PlayerState.Playing(
                        currentTime = getCurrentPlayerPosition(),
                        isFavourite = track.isFavourite
                    )
                )
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