package com.practicum.playlistmaker.mediaLibrary.data.tracks

import com.practicum.playlistmaker.mediaLibrary.data.db.dao.FeaturedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.convectors.TrackDbConvertor
import com.practicum.playlistmaker.mediaLibrary.domain.TracksDbRepository
import com.practicum.playlistmaker.search.domain.models.Track

class TracksDbRepositoryImpl(
    private val tracksDao: TrackDao,
    private val playlistTracksDao: PlaylistTrackDao,
    private val featuredTracksDao: FeaturedTrackDao,

    private val trackDbConvertor: TrackDbConvertor
) : TracksDbRepository {
    override suspend fun deleteTrackIfUnused(trackId: Int) {
        if(!playlistTracksDao.exists(trackId) && !featuredTracksDao.exists(trackId)){
            tracksDao.deleteTrack(trackId)
        }
    }

    override suspend fun saveTrack(track: Track) {
        tracksDao.insertTrack(trackDbConvertor.map(track))
    }
}