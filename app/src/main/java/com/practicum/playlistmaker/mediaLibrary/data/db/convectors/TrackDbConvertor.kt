package com.practicum.playlistmaker.mediaLibrary.data.db.convectors

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.FeaturedTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
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

    fun map(trackId: Int): FeaturedTrackEntity {
        return FeaturedTrackEntity(
            trackId = trackId,
            addedAt = System.currentTimeMillis()
        )
    }

    fun map(trackId: Int, playlistId: Int): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = trackId,
            playlistId = playlistId,
            addedAt = System.currentTimeMillis()
        )
    }
}