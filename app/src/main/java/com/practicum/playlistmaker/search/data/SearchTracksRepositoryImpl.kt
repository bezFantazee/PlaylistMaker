package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.SearchTracksRepository

class SearchTracksRepositoryImpl(private val networkClient: NetworkClient):
    SearchTracksRepository {
    override fun searchTracks(expression: String): SearchResult {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when {
            response.resultCode == 200 -> {
                val tracks = (response as TracksResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
                if (tracks.isNotEmpty()) SearchResult.Success(tracks)
                else SearchResult.NoResults
            }
            else -> SearchResult.NetWorkError
        }
    }
}