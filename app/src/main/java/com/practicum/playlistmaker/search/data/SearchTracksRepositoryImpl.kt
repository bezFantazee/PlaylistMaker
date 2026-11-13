package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.mediaLibrary.data.db.dao.FeaturedTrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistTrackDao
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.domain.models.SearchResult
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.SearchTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchTracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val featuredTrackDatabaseDao: FeaturedTrackDao,
    private val playlistTrackDao: PlaylistTrackDao
    ):
    SearchTracksRepository {
    override fun searchTracks(expression: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when {
            response.resultCode == 200 -> {
                with(response as TracksResponse){
                    val featuredTracks = featuredTrackDatabaseDao.getFeaturedTracksId()
                    val playlistTracks = playlistTrackDao.getAllPlaylistsTracksId()
                    val data = results.map {
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
                            it.previewUrl,
                            it.trackId in featuredTracks,
                            it.trackId in playlistTracks
                        )
                    }
                    if (data.isNotEmpty()) {
                        emit(SearchResult.Success(data))
                    }
                    else emit(SearchResult.NoResults)
                }
            }
            else -> emit(SearchResult.NetWorkError)
        }
    }
}