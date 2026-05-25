package com.practicum.playlistmaker.playlist.ui.view_model

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.playlist.domain.FileStorageInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val fileStorageInteractor: FileStorageInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : CreatePlaylistViewModel(fileStorageInteractor, playlistsInteractor) {
    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist)
        }
    }

    fun setInitialImage(path: String?) {
        imagePath.postValue(path)
    }
}