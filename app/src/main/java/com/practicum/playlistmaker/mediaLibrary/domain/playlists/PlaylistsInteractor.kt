package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylistById(playlistId: Int): Flow<Playlist?>

    suspend fun saveTrack(playlistId: Int, track: Track)
    suspend fun deleteTrack(playlistId: Int, trackId: Int)
    suspend fun getAllPlaylistsTrackIds(): List<Int>
    suspend fun getPlaylistTracksId(playlistId: Int): List<Int>
    fun getPlaylistTracksByPlaylistId(playlistId: Int): Flow<List<Track>>
}