package com.practicum.playlistmaker.playlist.domain

import android.net.Uri

interface FileStorageInteractor {
    suspend fun saveImage(uri: Uri): String
    suspend fun getImage(playlistId: String): Uri
}