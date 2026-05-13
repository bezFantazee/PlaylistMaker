package com.practicum.playlistmaker.player.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SingleLiveEvent
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsDbInteractor
import com.practicum.playlistmaker.player.domain.OnTrackCompletionListener
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.TrackCompletionListenerHolder
import com.practicum.playlistmaker.player.ui.PlayerEvent
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.player.ui.PlayerUiState
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
    private val featuredTracksInteractor: FeaturedTracksInteractor,
    private val playlistsDbInteractor: PlaylistsDbInteractor
) : ViewModel() {
    //LiveData
    private val playerStateLiveData = MutableLiveData(
        PlayerUiState(
            playerState = PlayerState.Default(isFavourite = track.isFavourite),
            isBottomSheetExpanded = false
        )
    )
    fun observePlayerState(): LiveData<PlayerUiState> = playerStateLiveData

    //Toast
    private val showToast = SingleLiveEvent<PlayerEvent?>()
    fun observeShowToast(): LiveData<PlayerEvent?> = showToast

    private var timerJob: Job? = null

    init {
        listenerHolder.delegate = object : OnTrackCompletionListener {
            override fun OnTrackPrepared() {
                playerStateLiveData.value = PlayerUiState(
                    playerState = PlayerState.Prepared(isFavourite = track.isFavourite),
                    isBottomSheetExpanded = false
                )
            }

            override fun onTrackCompleted() {
                pauseTimer()
                playerStateLiveData.value = PlayerUiState(
                    playerState = PlayerState.Completed(isFavourite = track.isFavourite),
                    isBottomSheetExpanded = false
                )
            }
        }
        preparePlayer()
    }

    //botomSheet
    fun hideBottomSheet() {
        updateUiState { it.copy(
            isBottomSheetExpanded = false
        ) }
    }

    //добавление в плейлист
    fun showBottomSheet() {
        updateUiState { it.copy(
            isBottomSheetExpanded = true
        ) }
        viewModelScope.launch { loadPlaylists() }
    }

    private suspend fun loadPlaylists() {
        playlistsDbInteractor.getPlaylists()
            .collect { result ->
                updateUiState { it.copy(
                    playlists = result
                ) }
            }
    }

    fun saveToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val playlistTracks = playlistsDbInteractor.getPlaylistTracksId(playlist.playlistId)
            if(track.trackId !in playlistTracks) {
                playlistsDbInteractor.saveTrack(playlist, track)
                hideBottomSheet()
                showToast.postValue(
                    PlayerEvent.ShowToast(
                        R.string.track_added_to_playlist,
                        listOf(playlist.playlistName)
                    )
                )
            } else {
                showToast.postValue(
                    PlayerEvent.ShowToast(
                        R.string.track_already_in_playlist,
                        listOf(playlist.playlistName)
                    )
                )
            }
        }
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
        when(playerStateLiveData.value!!.playerState) {
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

            val currentUIState = playerStateLiveData.value ?: return@launch
            val currentPlayerState = currentUIState.playerState

            val newPlayerState = when (currentPlayerState) {
                is PlayerState.Default -> currentPlayerState.copy(isFavourite = newFavouriteState)
                is PlayerState.Prepared -> currentPlayerState.copy(isFavourite = newFavouriteState)
                is PlayerState.Playing -> currentPlayerState.copy(isFavourite = newFavouriteState)
                is PlayerState.Paused -> currentPlayerState.copy(isFavourite = newFavouriteState)
                is PlayerState.Completed -> currentPlayerState.copy(isFavourite = newFavouriteState)
            }

           playerStateLiveData.value = currentUIState.copy(playerState = newPlayerState)
        }
        
    }

    //функции для управления медиаплеером
    private fun startPlayer() {
        playerInteractor.startPlayer()
        updateUiState { it.copy(
            playerState = PlayerState.Playing(
                currentTime = getCurrentPlayerPosition(),
                isFavourite = track.isFavourite
            )
        ) }
        startTimerUpdate()
    }
    private fun pausePlayer() {
        pauseTimer()
        playerInteractor.pausePlayer()
        pauseTimer()
        updateUiState { it.copy(
            playerState = PlayerState.Paused(
                currentTime = getCurrentPlayerPosition(),
                isFavourite = track.isFavourite
            )
        ) }
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
                updateUiState { it.copy(
                    playerState = PlayerState.Playing(
                        currentTime = getCurrentPlayerPosition(),
                        isFavourite = track.isFavourite
                    )
                ) }
            }
        }
    }
    private fun pauseTimer() {
        timerJob?.cancel()
    }
    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentTime()) ?: "00:00"
    }

    //функции-помощники
    private fun updateUiState(transform: (PlayerUiState) -> PlayerUiState) {
        val current = playerStateLiveData.value ?: return
        playerStateLiveData.value = transform(current)
    }

    companion object {
        private const val UPDATE_TIMETRACK_TIME = 300L
    }
}