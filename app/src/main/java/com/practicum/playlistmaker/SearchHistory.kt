package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class SearchHistory(val sharePref: SharedPreferences) {
    fun getTracks(): MutableList<Track> {
        val tracks = sharePref.getString(TRACK_KEY, null)
        return if (tracks != null) {
            createTracksListFromJson(tracks)
        } else {
            mutableListOf()
        }
    }

    fun saveTrack(track: Track) {
        val tracks = getTracks()
        val track_index = tracks.indexOf(track)
        if(track_index == -1){
            tracks.add(0, track)
        } else {
            tracks.removeAt(track_index)
            tracks.add(0, track)
        }

        if (tracks.size > 10){
            tracks.removeAt(tracks.lastIndex)
        }

        sharePref.edit()
            .putString(TRACK_KEY, createJsonFromTracksList(tracks))
            .apply()
    }

    fun clearHistory() {
        sharePref.edit()
            .clear()
            .apply()
    }

    private fun createTracksListFromJson(json: String): MutableList<Track> {
        return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
    }

    private fun createJsonFromTracksList(tracks: List<Track>): String {
        return Gson().toJson(tracks)
    }

    companion object{
        private const val TRACK_KEY = "tracks_key"
    }
}