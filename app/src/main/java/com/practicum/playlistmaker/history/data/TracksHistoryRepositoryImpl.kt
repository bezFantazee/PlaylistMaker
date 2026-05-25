package com.practicum.playlistmaker.history.data

import com.practicum.playlistmaker.history.domain.TracksHistoryRepository
import com.practicum.playlistmaker.mediaLibrary.data.db.FeaturedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.SavedTrackDao
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksHistoryRepositoryImpl(
    private val prefStorageClient: StorageClient<MutableList<Track>>,
    private val featuredTrackDatabaseDao: FeaturedTrackDao,
    private val savedTrackDatabaseDao: SavedTrackDao
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

    override suspend fun getTracks(): Flow<List<Track>> = flow{
        val featuredIds = featuredTrackDatabaseDao.getFeaturedTracksId().toSet()
        val savedIds = savedTrackDatabaseDao.getSavedTracksId().toSet()
        val tracks = prefStorageClient.get() ?: emptyList()

        if (tracks.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        emit(
            tracks.map { track ->
            track.copy(
                isFavourite = track.trackId in featuredIds,
                isSaved = track.trackId in savedIds)
            }.toMutableList()
        )
    }

    override fun clearSavedTracks() {
        prefStorageClient.clear()
    }
}