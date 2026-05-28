package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPlaylistTrack(track: PlaylistTrackEntity)
    @Delete
    suspend fun deletePlaylistTrack(track: PlaylistTrackEntity)
    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getPlaylistTracksId(playlistId: Int): List<Int>

    @Query("SELECT DISTINCT trackId FROM playlist_tracks")
    suspend fun getAllPlaylistsTracksId(): List<Int>

    @Query("""
        SELECT t.*
        FROM playlist_tracks AS pt
        INNER JOIN tracks AS t ON t.trackId = pt.trackId
        WHERE pt.playlistId = :playlistId
    """)
    fun getPlaylistTracks(playlistId: Int): Flow<List<TrackEntity>>

    @Query("""
        SELECT EXISTS(
            SELECT 1
            FROM playlist_tracks
            WHERE trackId = :trackId
        )
    """)
    suspend fun exists(trackId: Int): Boolean
}