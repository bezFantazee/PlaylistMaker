package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsDbRepository
) : PlaylistsInteractor{
    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistTracksId(playlistId: Int): List<Int> {
        return repository.getPlaylistTracksId(playlistId) ?: emptyList()
    }

    override suspend fun saveTrack(playlist: Playlist, track: Track) {
        repository.saveTrack(track)
        repository.addTrackToPlaylist(playlist, track.trackId)
    }

    override suspend fun getSavedTrackIds(): List<Int> {
        return repository.getSavedTrackIds()
    }
}