package com.practicum.playlistmaker.playlist.data

import android.net.Uri

interface FileStorage {
    suspend fun saveImage(uri: Uri): String
    suspend fun getImage(fileName: String): Uri
}