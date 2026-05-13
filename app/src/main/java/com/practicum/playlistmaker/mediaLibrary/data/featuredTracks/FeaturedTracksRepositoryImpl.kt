package com.practicum.playlistmaker.mediaLibrary.data.featuredTracks

import com.practicum.playlistmaker.mediaLibrary.data.db.FeaturedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FeaturedTrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.featuredTracks.FeaturedTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FeaturedTracksRepositoryImpl(
    private val trackDatabaseDao: FeaturedTrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FeaturedTracksRepository {
    override suspend fun addTrack(track: Track) {
        val trackEntity = trackDbConvertor.mapToEntity(track) { t ->
            FeaturedTrackEntity(
                trackId = t.trackId,
                trackName = t.trackName,
                artistName = t.artistName,
                trackTimeMillis = t.trackTimeMillis,
                artworkUrl100 = t.artworkUrl100,
                collectionName = t.collectionName,
                releaseDate = t.releaseDate,
                primaryGenreName = t.primaryGenreName,
                country = t.country,
                previewUrl = t.previewUrl,
                addedAt = System.currentTimeMillis()
            )
        }
        trackDatabaseDao.insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = trackDbConvertor.mapToEntity(track) { t ->
            FeaturedTrackEntity(
                trackId = t.trackId,
                trackName = t.trackName,
                artistName = t.artistName,
                trackTimeMillis = t.trackTimeMillis,
                artworkUrl100 = t.artworkUrl100,
                collectionName = t.collectionName,
                releaseDate = t.releaseDate,
                primaryGenreName = t.primaryGenreName,
                country = t.country,
                previewUrl = t.previewUrl,
                addedAt = System.currentTimeMillis()
            )
        }
        trackDatabaseDao.deleteTrack(trackEntity)
    }

    override fun getFeaturedTracks(): Flow<List<Track>> {
        return trackDatabaseDao.getFeaturedTracks()
            .map { entities ->
            entities.map { trackEntity -> trackDbConvertor.mapToTrack(
                trackId = trackEntity.trackId,
                trackName = trackEntity.trackName,
                artistName = trackEntity.artistName,
                trackTimeMillis = trackEntity.trackTimeMillis,
                country = trackEntity.country,
                primaryGenreName = trackEntity.primaryGenreName,
                releaseDate = trackEntity.releaseDate,
                artworkUrl100 = trackEntity.artworkUrl100,
                collectionName = trackEntity.collectionName,
                previewUrl = trackEntity.previewUrl
                )
                }
            }
    }
}