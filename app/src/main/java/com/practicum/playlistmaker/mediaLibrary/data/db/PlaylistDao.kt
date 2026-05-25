package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity): Int

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT tracks FROM playlists WHERE playlistId = :playlistId")
    suspend fun getPlaylistTracksId(playlistId: Int): String?

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    fun getPlaylist(playlistId: Int): Flow<PlaylistEntity?>

    @Query("SELECT tracks FROM playlists")
    fun getAllPlaylistsTracks(): List<String?>
}