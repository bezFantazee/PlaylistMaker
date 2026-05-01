package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class])
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}