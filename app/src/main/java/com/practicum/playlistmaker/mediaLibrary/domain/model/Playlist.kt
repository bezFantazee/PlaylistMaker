package com.practicum.playlistmaker.mediaLibrary.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val tracksCount: Int
): Parcelable
