package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest

class RetrofitNetworkClient(
    private val tracksService: TrackApiService
): NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            try {
                val resp = tracksService.searchTracks(
                    dto.expression
                )

                resp.apply { resultCode = 200 }
            } catch (e: Exception) { //если нет интернета
                Response().apply { resultCode = 0 } // 0 для обозначения сетевой ошибки
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}