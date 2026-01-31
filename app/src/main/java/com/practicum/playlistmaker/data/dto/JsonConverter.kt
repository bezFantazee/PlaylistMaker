package com.practicum.playlistmaker.data.dto

import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track

fun createTracksListFromJson(json: String): MutableList<Track> {
    return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
}

fun createJsonFromTracksList(tracks: List<Track>): String {
    return Gson().toJson(tracks)
}