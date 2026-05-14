package com.practicum.playlistmaker.playlist.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsInteractor
import com.practicum.playlistmaker.playlist.domain.FileStorageInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val fileStorageInteractor: FileStorageInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val imagePath = MutableLiveData<String?>()
    fun observeImagePath(): LiveData<String?> = imagePath

    fun savePlaylist(playlistName: String, playlistDescription: String?, imagePath: String?){
        viewModelScope.launch {
            val playlist = Playlist(
                playlistId = 0,
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                imagePath = imagePath,
                tracks = null,
                tracksCount = 0
            )
            playlistsInteractor.savePlaylist(playlist)
        }
    }

    fun setImage(uri: Uri?) {
        viewModelScope.launch {
            val path = saveImageToStorage(uri)
            imagePath.postValue(path)
        }
    }

    private suspend fun saveImageToStorage(uri: Uri?): String? {
        if (uri == null) return null
        return fileStorageInteractor.saveImage(uri)
    }
}