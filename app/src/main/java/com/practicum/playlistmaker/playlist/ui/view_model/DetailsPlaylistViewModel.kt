package com.practicum.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SingleLiveEvent
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsPlaylistViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val playlistState = MutableLiveData<DetailsPlaylistState>()
    fun observePlaylistState(): LiveData<DetailsPlaylistState> = playlistState

    private val showToast = SingleLiveEvent<DetailPlaylistEvent?>()
    fun observeShowToast(): LiveData<DetailPlaylistEvent?> = showToast

    init{
        loadPlaylist()
    }
    fun refreshPlaylist() {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            combine(
                playlistsInteractor.getPlaylistById(playlistId),
                playlistsInteractor.getPlaylistTracksByPlaylistId(playlistId)
            ) { playlist, tracks ->
                if(playlist == null) {
                    DetailsPlaylistState.Empty
                } else {
                    DetailsPlaylistState.Content(
                        playlist = playlist,
                        tracks = tracks,
                        tracksFullTime = formatTime(
                            tracks.sumOf {
                            it.trackTimeMillis
                        })
                    )
                }
            }.collect { state ->
                playlistState.value = state
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track){
        val currentState = playlistState.value

        if(currentState is DetailsPlaylistState.Content){
            val currentPlaylist = currentState.playlist

            viewModelScope.launch {
               playlistsInteractor.deleteTrack(currentPlaylist.playlistId, track.trackId)
            }
        }
    }

    fun sharePlaylist() {
        val currentState = playlistState.value

        if(currentState is DetailsPlaylistState.Content) {
            val currentTracks = currentState.tracks
            val currentPlaylist = currentState.playlist

            if(currentTracks.isNullOrEmpty()){
                showToast.postValue(
                    DetailPlaylistEvent.ShowToast(
                        R.string.on_share_empty_playlist
                    )
                )
                return
            } else {
                sharingInteractor.sharePlaylist(
                    currentPlaylist.playlistName,
                    currentPlaylist.playlistDescription,
                    currentState.tracks)
            }
        }
    }

    fun deletePlaylist(){
        val currentState = playlistState.value

        if(currentState is DetailsPlaylistState.Content){
            val currentPlaylist = currentState.playlist

            viewModelScope.launch {
                playlistsInteractor.deletePlaylist(currentPlaylist)
            }
        }
    }

    fun showMenu(){
        val currentState = playlistState.value

        if(currentState is DetailsPlaylistState.Content) {
            playlistState.value = currentState.copy(
                isMenuExpanded = true
            )
        }
    }

    fun hideMenu(){
        val currentState = playlistState.value

        if(currentState is DetailsPlaylistState.Content) {
            playlistState.value = currentState.copy(
                isMenuExpanded = false
            )
        }
    }

    private fun formatTime(millis: Long): String {
        return SimpleDateFormat("m", Locale.getDefault()).format(millis)
    }
}