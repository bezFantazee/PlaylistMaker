package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistTracksId(playlistId: Int): List<Int>

    suspend fun saveTrack(playlist: Playlist, track: Track)
    suspend fun deleteTrack(playlist: Playlist, track: Track)

    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getSavedTrackIds(): List<Int>

    fun getPlaylistById(playlistId: Int): Flow<Playlist?>

    fun getPlaylistTracks(trackIds: List<Int>): Flow<List<Track>>
    fun getPlaylistTracksByPlaylistId(playlistId: Int): Flow<List<Track>>
}