package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FeaturedTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeaturedTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FeaturedTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FeaturedTrackEntity)

    @Query("SELECT * FROM featured_tracks ORDER BY addedAt DESC")
    fun getFeaturedTracks(): Flow<List<FeaturedTrackEntity>>

    @Query("SELECT trackId FROM featured_tracks")
    suspend fun getFeaturedTracksId(): List<Int>
}