package com.practicum.playlistmaker.mediaLibrary.data.db

import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConvertor {
    fun <T> mapToEntity(track: Track, factory: (Track) -> T): T {
        return factory(track)
    }

    fun mapToTrack(
        trackId: Int,
        trackName: String,
        artistName: String,
        trackTimeMillis: Long,
        country: String,
        primaryGenreName: String,
        releaseDate: String?,
        artworkUrl100: String,
        collectionName: String,
        previewUrl: String?
    ): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            country = country,
            primaryGenreName = primaryGenreName,
            releaseDate = releaseDate,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            previewUrl = previewUrl
        )
    }
}