package com.practicum.playlistmaker.mediaLibrary.data.featuredTracks

import com.practicum.playlistmaker.mediaLibrary.data.db.dao.FeaturedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.convectors.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeaturedTracksRepositoryImpl(
    private val featuredTrackDao: FeaturedTrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FeaturedTracksRepository {
    override suspend fun addTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        val featuredTrackEntity = trackDbConvertor.map(track.trackId)
        featuredTrackDao.insertFeaturedTrack(featuredTrackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        featuredTrackDao.deleteFeaturedTrack(trackDbConvertor.map(track.trackId))
    }

    override fun getFeaturedTracks(): Flow<List<Track>> {
        return featuredTrackDao.getFeaturedTracks()
            .map { entities ->
            entities.map { trackEntity -> trackDbConvertor.map(trackEntity)
                }
            }
    }
}