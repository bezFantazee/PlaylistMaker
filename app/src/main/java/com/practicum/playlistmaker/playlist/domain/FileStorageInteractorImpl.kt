package com.practicum.playlistmaker.playlist.domain

import android.net.Uri

class FileStorageInteractorImpl(
    private val repository: FileStorageRepository
) : FileStorageInteractor{
    override suspend fun saveImage(uri: Uri): String {
        return repository.saveImage(uri)
    }

    override suspend fun getImage(playlistId: String): Uri {
        return repository.getImage(playlistId)
    }
}