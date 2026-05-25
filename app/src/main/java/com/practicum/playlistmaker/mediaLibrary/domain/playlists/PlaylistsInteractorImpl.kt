package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import android.util.Log
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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

    override suspend fun deleteTrack(
        playlist: Playlist,
        track: Track
    ) {

        withContext(Dispatchers.IO) {

            repository.deleteTrackFromPlaylist(
                playlist,
                track.trackId
            )

            val allPlaylistsTracks =
                repository.getAllPlaylistsTracks()

            if (track.trackId !in allPlaylistsTracks) {
                repository.deleteTrack(track)
            }
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            repository.deletePlaylist(playlist)

            val trackIdsToDelete = playlist.tracks ?: return@withContext
            if (trackIdsToDelete.isEmpty()) return@withContext

            val allRemainingTracksSet = repository.getAllPlaylistsTracks().toSet()

            val tracks = repository.getPlaylistTracks(trackIdsToDelete).first()

            tracks.forEach { track ->
                if (track.trackId !in allRemainingTracksSet) {
                    repository.deleteTrack(track)
                }
            }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getSavedTrackIds(): List<Int> {
        return repository.getSavedTrackIds()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return repository.getPlaylist(playlistId)
    }

    override fun getPlaylistTracks(trackIds: List<Int>): Flow<List<Track>> {
        return repository.getPlaylistTracks(trackIds)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPlaylistTracksByPlaylistId(playlistId: Int): Flow<List<Track>> {
        return repository.getPlaylist(playlistId)
            .flatMapLatest { playlist ->
                repository.getPlaylistTracks(
                    playlist?.tracks ?: emptyList()
                )
            }
    }
}