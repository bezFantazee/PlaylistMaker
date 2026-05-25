package com.practicum.playlistmaker.playlist.data

import android.net.Uri
import com.practicum.playlistmaker.playlist.domain.FileStorageRepository

class FileStorageRepositoryImpl(
    private val fileStorage: FileStorage
) : FileStorageRepository{
    override suspend fun saveImage(uri: Uri): String {
        return fileStorage.saveImage(uri)
    }

    override suspend fun getImage(fileName: String): Uri {
        return fileStorage.getImage(fileName)
    }
}