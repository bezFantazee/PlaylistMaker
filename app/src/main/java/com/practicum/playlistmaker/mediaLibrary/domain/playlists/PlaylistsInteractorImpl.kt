package com.practicum.playlistmaker.mediaLibrary.domain.playlists

import com.practicum.playlistmaker.mediaLibrary.domain.TracksDbRepository
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext

class PlaylistsInteractorImpl(
    private val playlistRepository: PlaylistsDbRepository,
    private val tracksRepository: TracksDbRepository
) : PlaylistsInteractor{
    override suspend fun savePlaylist(playlist: Playlist) {
        playlistRepository.savePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun getPlaylistTracksId(playlistId: Int): List<Int> {
        return playlistRepository.getPlaylistTracksId(playlistId) ?: emptyList()
    }

    override suspend fun saveTrack(playlistId: Int, track: Track) {
        tracksRepository.saveTrack(track)
        playlistRepository.addTrackToPlaylist(playlistId, track.trackId)
    }

    override suspend fun deleteTrack(
        playlistId: Int,
        trackId: Int
    ) {
        playlistRepository.deleteTrackFromPlaylist(trackId, playlistId)
        tracksRepository.deleteTrackIfUnused(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {

        val trackIds = playlistRepository.getPlaylistTracksId(playlist.playlistId)

        playlistRepository.deletePlaylist(playlist)

        trackIds?.forEach { trackId ->
            tracksRepository.deleteTrackIfUnused(trackId)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylistsTrackIds(): List<Int> {
        return playlistRepository.getAllPlaylistsTracksId()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist?> {
        return playlistRepository.getPlaylist(playlistId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPlaylistTracksByPlaylistId(playlistId: Int): Flow<List<Track>> {
        return playlistRepository.getPlaylistTracks(playlistId)
    }
}