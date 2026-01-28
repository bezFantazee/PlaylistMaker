package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {

    private val trackBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(trackBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksService = retrofit.create(TrackApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            return try {
                val resp = tracksService.searchTracks(dto.expression).execute()

                val body = resp.body() ?: Response()

                body.apply { resultCode = resp.code() }
            } catch (e: Exception) { //если нет интернета
                Response().apply { resultCode = 0 } // 0 для обозначения сетевой ошибки
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

}