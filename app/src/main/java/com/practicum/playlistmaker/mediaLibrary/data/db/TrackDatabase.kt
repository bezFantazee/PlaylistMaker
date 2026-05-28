package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.FeaturedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FeaturedTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity

@Database(version = 6, entities = [TrackEntity::class, FeaturedTrackEntity::class, PlaylistTrackEntity::class, PlaylistEntity::class])
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun featuredTrackDao(): FeaturedTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}