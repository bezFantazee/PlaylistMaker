package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackApiService {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text:String): Call<TracksResponse>
}