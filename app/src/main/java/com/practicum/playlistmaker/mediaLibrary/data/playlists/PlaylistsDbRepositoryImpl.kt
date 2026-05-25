package com.practicum.playlistmaker.mediaLibrary.data.playlists

import android.util.Log
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

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackIdToDelete: Int) {
        val updatedTracks = playlist.tracks?.filterNot { trackId -> trackId == trackIdToDelete }
        val playlistEntity = playlistConvector.map(playlist.copy(tracks = updatedTracks, tracksCount = playlist.tracksCount - 1))
        playlistsDatabaseDao.updatePlaylist(playlistEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = trackConvector.mapToEntity(track) {t ->
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
        trackDatabaseDao.deleteTrack(trackEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvector.map(playlist)
        playlistsDatabaseDao.deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvector.map(playlist)
        playlistsDatabaseDao.updatePlaylist(playlistEntity)
    }

    override suspend fun getAllPlaylistsTracks(): List<Int> {
        return playlistsDatabaseDao
            .getAllPlaylistsTracks()
            .flatMap { tracksId ->
                tracksId?.let {
                    playlistConvector.map(it)
                } ?: emptyList()
            }
            .distinct()
    }

    override suspend fun getSavedTrackIds(): List<Int> {
        return trackDatabaseDao.getSavedTracksId()
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist?> {
        val playlistEntity = playlistsDatabaseDao.getPlaylist(playlistId)
        return playlistEntity.map { entity ->
            if(entity != null){
                playlistConvector.map(entity)
            } else {
                null
            }
        }
    }

    override fun getPlaylistTracks(trackIds: List<Int>): Flow<List<Track>> {
        val allSavedTracks = trackDatabaseDao.getSavedTracks()
        return allSavedTracks.map { entities ->
            val tracksMap = entities.associateBy { it.trackId }

            trackIds
                .asReversed()
                .mapNotNull { trackId ->
                    tracksMap[trackId]
                }
                .map { entity ->
                    trackConvector.mapToTrack(
                        trackId = entity.trackId,
                        trackName = entity.trackName,
                        artistName = entity.artistName,
                        trackTimeMillis = entity.trackTimeMillis,
                        country = entity.country,
                        primaryGenreName = entity.primaryGenreName,
                        releaseDate = entity.releaseDate,
                        artworkUrl100 = entity.artworkUrl100,
                        collectionName = entity.collectionName,
                        previewUrl = entity.previewUrl
                    )
                }
        }
    }
}