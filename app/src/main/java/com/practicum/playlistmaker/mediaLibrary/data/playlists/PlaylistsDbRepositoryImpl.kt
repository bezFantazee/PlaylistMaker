package com.practicum.playlistmaker.mediaLibrary.data.playlists

import android.util.Log
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediaLibrary.data.db.convectors.PlaylistDbConvector
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.convectors.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.domain.playlists.PlaylistsDbRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsDbRepositoryImpl(
    private val playlistsDatabaseDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistConvector: PlaylistDbConvector,
    private val trackConvector: TrackDbConvertor
) : PlaylistsDbRepository {
    //работа с плейлистами
    override suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvector.map(playlist)
        playlistsDatabaseDao.insertPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        val playlistEntities = playlistsDatabaseDao.getPlaylistsWithCount()
        return playlistEntities.map {
            it.map { entity ->
                playlistConvector.map(entity)
            }
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvector.map(playlist)
        playlistsDatabaseDao.deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvector.map(playlist)
        playlistsDatabaseDao.updatePlaylist(playlistEntity)
    }

    override fun getPlaylist(playlistId: Int): Flow<Playlist?> {
        val playlistEntity = playlistsDatabaseDao.getPlaylistWithCount(playlistId)
        return playlistEntity.map { entity ->
            playlistConvector.map(entity)
        }
    }

    //работа с треками
    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        playlistTrackDao.deletePlaylistTrack(trackConvector.map(trackId, playlistId))
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int) {
        val playlistTrackEntity = trackConvector.map(trackId, playlistId)
        playlistTrackDao.insertPlaylistTrack(playlistTrackEntity)
    }

    override suspend fun getAllPlaylistsTracksId(): List<Int> {
        return playlistTrackDao.getAllPlaylistsTracksId()
    }

    override suspend fun getPlaylistTracksId(playlistId: Int): List<Int> {
        return playlistTrackDao.getPlaylistTracksId(playlistId)
    }

    override fun getPlaylistTracks(playlistId: Int): Flow<List<Track>> {
        return playlistTrackDao.getPlaylistTracks(playlistId).map {
            it.map { entity ->
                trackConvector.map(entity)
            }
        }
    }
}