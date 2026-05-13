package com.practicum.playlistmaker.mediaLibrary.domain.model

import com.practicum.playlistmaker.search.domain.models.Track

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val tracks: List<Int>?,
    val tracksCount: Int
)
