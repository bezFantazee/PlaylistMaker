package com.practicum.playlistmaker.mediaLibrary.ui.presenter.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {
    private val playlistsUiStateLiveData = MutableLiveData<PlaylistsUiState>(
        PlaylistsUiState.Empty
    )

    fun observePlaylistsUiState(): LiveData<PlaylistsUiState> = playlistsUiStateLiveData

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect { result ->
                    if(result.isNotEmpty()) {
                        playlistsUiStateLiveData.value = PlaylistsUiState.Content(result)
                    } else {
                        playlistsUiStateLiveData.value = PlaylistsUiState.Empty
                    }
                }
        }
    }
}