package com.practicum.playlistmaker.mediaLibrary.data

import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.FeaturedTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FeaturedTracksRepositoryImpl(
    private val trackDatabase: TrackDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FeaturedTracksRepository {
    override suspend fun addTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        trackDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        trackDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getFeaturedTracks(): Flow<List<Track>> {
        return trackDatabase.trackDao().getFeaturedTracks()
            .map { entities ->
            entities.map { trackEntity -> trackDbConvertor.map(trackEntity) }
            }
    }
}