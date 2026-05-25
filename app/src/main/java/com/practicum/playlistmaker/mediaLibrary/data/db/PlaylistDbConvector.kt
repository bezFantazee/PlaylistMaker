package com.practicum.playlistmaker.mediaLibrary.data.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDbConvector(
    private val gson: Gson
) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracks = listToString(playlist.tracks),
            tracksCount = playlist.tracks?.size ?: 0
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            tracks = stringToList(playlist.tracks),
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