package com.practicum.playlistmaker.mediaLibrary.data.playlists

import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistDao
import com.practicum.playlistmaker.mediaLibrary.data.db.PlaylistDbConvector
import com.practicum.playlistmaker.mediaLibrary.data.db.SavedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.SavedTrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsDbRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsDbRepositoryImpl(
    private val playlistsDatabaseDao: PlaylistDao,
    private val trackDatabaseDao: SavedTrackDao,
    private val playlistConvector: PlaylistDbConvector,
    private val trackConvector: TrackDbConvertor
) : PlaylistsDbRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvector.map(playlist)
        playlistsDatabaseDao.insertPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        val playlistEntities = playlistsDatabaseDao.getPlaylists()
        return playlistEntities.map { entities ->
            entities.map { entity -> playlistConvector.map(entity)}
        }
    }

    override suspend fun getPlaylistTracksId(playlistId: Int): List<Int>? {
        return playlistConvector.map(playlistsDatabaseDao.getPlaylistTracksId(playlistId) ?: "")
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, trackId: Int) {
        val updatedTracks = playlist.tracks?.plus(trackId) ?: listOf(trackId)
        val playlistEntity = playlistConvector.map(playlist.copy(tracks = updatedTracks))
        val res = playlistsDatabaseDao.updatePlaylist(playlistEntity)
    }

    override suspend fun saveTrack(track: Track) {
        val trackEntity = trackConvector.mapToEntity(track) { t ->
            SavedTrackEntity(
                trackId = t.trackId,
                trackName = t.trackName,
                artistName = t.artistName,
                trackTimeMillis = t.trackTimeMillis,
                artworkUrl100 = t.artworkUrl100,
                collectionName = t.collectionName,
                releaseDate = t.releaseDate,
                primaryGenreName = t.primaryGenreName,
                country = t.country,
                previewUrl = t.previewUrl,
                addedAt = System.currentTimeMillis()
            )
        }
        trackDatabaseDao.insertTrack(trackEntity)
    }

    override suspend fun getSavedTrackIds(): List<Int> {
        return trackDatabaseDao.getSavedTracksId()
    }
}