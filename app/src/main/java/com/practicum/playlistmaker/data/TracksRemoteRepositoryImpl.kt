package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.domain.api.TracksRemoteRepository
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track

class TracksRemoteRepositoryImpl(private val networkClient: NetworkClient):
    TracksRemoteRepository {
    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when {
            response.resultCode == 200 -> {
                val tracks = (response as TracksResponse).results.map {
                    Track(it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl)
                }
                if (tracks.isNotEmpty()) SearchResult.Success(tracks)
                else SearchResult.NoResults
            }
            else -> SearchResult.NetWorkError
        }
    }
}