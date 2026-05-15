package com.practicum.playlistmaker.playlist.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FileInternalStorage(
    private val context: Context
) : FileStorage {
    private val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_album")

    override suspend fun saveImage(uri: Uri): String {
        if(!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(filePath, fileName)

        val inputStream = if (uri.scheme == "content") {
            context.contentResolver.openInputStream(uri)
        } else {
            java.io.FileInputStream(File(uri.path ?: ""))
        }

        inputStream?.use { input ->
            FileOutputStream(file).use { outputStream ->
                val bitmap = BitmapFactory.decodeStream(input)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }
        return file.absolutePath
    }

    override suspend fun getImage(fileName: String): Uri {
        val file = File(filePath, fileName)
        return file.toUri()
    }
}