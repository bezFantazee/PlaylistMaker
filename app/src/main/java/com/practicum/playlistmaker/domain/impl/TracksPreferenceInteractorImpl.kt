package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.preferences.TrackPreferenceInteractor
import com.practicum.playlistmaker.domain.preferences.TracksLocalRepository

class TracksPreferenceInteractorImpl(private val repository: TracksLocalRepository): TrackPreferenceInteractor {
    override fun clearSavedTracks() {
        repository.clearSavedTracks()
    }

    override fun getTracks(key: String): MutableList<Track> {
        return repository.getTracks(key)
    }

    override fun saveTrack(track: Track, key: String) {
        val tracks = getTracks(key)
        val trackIndex = tracks.indexOf(track)
        if(trackIndex == -1){
            tracks.add(0, track)
        }
        if (tracks.size > 10){
            tracks.removeAt(tracks.lastIndex)
        }
        repository.saveTrack(tracks, key)
    }

}