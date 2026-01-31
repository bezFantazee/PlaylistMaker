package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TrackSearchRequest

class RetrofitNetworkClient: NetworkClient {
    private val tracksService = Creator.provideTrackApiService()

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