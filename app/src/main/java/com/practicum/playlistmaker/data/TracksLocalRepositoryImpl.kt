package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.createJsonFromTracksList
import com.practicum.playlistmaker.data.dto.createTracksListFromJson
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.preferences.TracksLocalRepository

class TracksLocalRepositoryImpl(private val sharedPreferencesClient: PreferencesClient): TracksLocalRepository {
    override fun saveTrack(tracks: List<Track>, key: String) {
        val json = createJsonFromTracksList(tracks)

        sharedPreferencesClient.save(json, key)
    }

    override fun getTracks(key: String): MutableList<Track> {
        val json = sharedPreferencesClient.get(key)
        return if(json != null){
            createTracksListFromJson(json)
        } else {
            mutableListOf()
        }
    }

    override fun clearSavedTracks() {
        sharedPreferencesClient.clear()
    }
}