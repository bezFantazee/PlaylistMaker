package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.mediaLibrary.data.db.dto.PlaylistWithCount
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity): Int

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("""
    SELECT p.playlistId,
           p.playlistName,
           p.playlistDescription,
           p.imagePath,
           COUNT(pt.trackId) as tracksCount
    FROM playlists p
    LEFT JOIN playlist_tracks pt
        ON p.playlistId = pt.playlistId
    GROUP BY p.playlistId
""")
    fun getPlaylistsWithCount(): Flow<List<PlaylistWithCount>>
    @Query("""
    SELECT p.playlistId,
           p.playlistName,
           p.playlistDescription,
           p.imagePath,
           COUNT(pt.trackId) as tracksCount
    FROM playlists p
    LEFT JOIN playlist_tracks pt
        ON p.playlistId = pt.playlistId
    WHERE p.playlistId = :playlistId
    GROUP BY p.playlistId
""")
    fun getPlaylistWithCount(playlistId: Int): Flow<PlaylistWithCount>
}