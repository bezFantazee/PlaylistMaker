package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistTracksId(playlistId: Int): List<Int>

    suspend fun saveTrack(playlist: Playlist, track: Track)
    suspend fun getSavedTrackIds(): List<Int>
}