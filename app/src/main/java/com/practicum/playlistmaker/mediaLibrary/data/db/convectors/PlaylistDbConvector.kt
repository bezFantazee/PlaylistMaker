package com.practicum.playlistmaker.mediaLibrary.data.db.convectors

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediaLibrary.data.db.dto.PlaylistWithCount
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistDbConvector(
    private val gson: Gson
) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath
        )
    }

    fun map(playlist: PlaylistWithCount): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracksCount = playlist.tracksCount
        )
    }

    fun map(tracksId: String): List<Int>? {
        return stringToList(tracksId)
    }

    private fun listToString(list: List<Int>?): String? {
        if (list == null) return null
        return gson.toJson(list)
    }

    private fun stringToList(string: String?): List<Int>? {
        if(string == null) return null
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(string, listType)
    }
}