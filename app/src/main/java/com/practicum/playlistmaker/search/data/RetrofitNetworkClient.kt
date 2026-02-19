package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(
    private val tracksService: TrackApiService
): NetworkClient {
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