package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbRepository {
    //работа с плейлистами
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylist(playlistId: Int): Flow<Playlist?>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)

    //работа с треками
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int)
    suspend fun getPlaylistTracksId(playlistId: Int): List<Int>?
    suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int)
    suspend fun getAllPlaylistsTracksId(): List<Int>
    fun getPlaylistTracks(playlistId: Int): Flow<List<Track>>
}