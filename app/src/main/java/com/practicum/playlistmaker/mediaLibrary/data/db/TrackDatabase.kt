package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FeaturedTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.SavedTrackEntity

@Database(version = 5, entities = [FeaturedTrackEntity::class, SavedTrackEntity::class, PlaylistEntity::class])
abstract class TrackDatabase : RoomDatabase() {
    abstract fun featuredTrackDao(): FeaturedTrackDao

    abstract fun savedTrackDao(): SavedTrackDao

    abstract fun playlistDao(): PlaylistDao
}