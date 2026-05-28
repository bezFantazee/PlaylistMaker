package com.practicum.playlistmaker.mediaLibrary.data.db.dto

data class PlaylistWithCount(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val tracksCount: Int
)
