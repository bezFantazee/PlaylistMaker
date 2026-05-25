package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.SavedTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: SavedTrackEntity)
    @Delete
    suspend fun deleteTrack(track: SavedTrackEntity)
    @Query("SELECT trackId FROM saved_tracks")
    suspend fun getSavedTracksId(): List<Int>

    @Query("SELECT * FROM saved_tracks")
    fun getSavedTracks(): Flow<List<SavedTrackEntity>>
}