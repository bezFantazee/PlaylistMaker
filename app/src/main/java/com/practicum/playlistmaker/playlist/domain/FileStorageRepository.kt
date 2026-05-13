package com.practicum.playlistmaker.playlist.domain

import android.net.Uri

interface FileStorageRepository {
    suspend fun saveImage(uri: Uri) : String
    suspend fun getImage(fileName: String): Uri
}