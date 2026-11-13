package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FeaturedTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeaturedTrackDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertFeaturedTrack(track: FeaturedTrackEntity)

    @Delete
    fun deleteFeaturedTrack(track: FeaturedTrackEntity)
    @Query("""
        SELECT t.* 
        FROM featured_tracks AS ft
        INNER JOIN tracks AS t 
        ON t.trackId = ft.trackId
        ORDER BY ft.addedAt DESC
    """)
    fun getFeaturedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM featured_tracks")
    suspend fun getFeaturedTracksId(): List<Int>

    @Query("""
        SELECT EXISTS(
            SELECT 1
            FROM featured_tracks
            WHERE trackId = :trackId
        )
    """)
    suspend fun exists(trackId: Int): Boolean
}