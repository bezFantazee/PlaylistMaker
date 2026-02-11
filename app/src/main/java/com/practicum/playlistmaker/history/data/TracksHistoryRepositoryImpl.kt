package com.practicum.playlistmaker.history.data

import com.practicum.playlistmaker.history.data.dto.createJsonFromTracksList
import com.practicum.playlistmaker.history.data.dto.createTracksListFromJson
import com.practicum.playlistmaker.history.domain.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlin.compareTo

class TracksHistoryRepositoryImpl(
    private val prefStorageClient: StorageClient<MutableList<Track>>
):
    TracksHistoryRepository {
    override fun saveTrack(track: Track) {
        val tracks = prefStorageClient.get() ?: mutableListOf()
        val trackIndex = tracks.indexOf(track)
        if(trackIndex == -1){
            tracks.add(0, track)
        }
        if (tracks.size > 10){
            tracks.removeAt(tracks.lastIndex)
        }
        prefStorageClient.save(tracks)
    }

    override fun getTracks(): MutableList<Track> {
        val tracks = prefStorageClient.get() ?: mutableListOf()
        return tracks
    }

    override fun clearSavedTracks() {
        prefStorageClient.clear()
    }
}