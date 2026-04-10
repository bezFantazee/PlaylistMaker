package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val tracksService: TrackApiService
): NetworkClient {
    override suspend fun doRequest(dto: Any): Response {

        if (dto is TrackSearchRequest) {
            return withContext(Dispatchers.IO){
                try {
                    val resp = tracksService.searchTracks(
                        dto.expression
                    )

                    resp.apply { resultCode = 200 }
                } catch (e: Exception) { //если нет интернета
                    Response().apply { resultCode = 0 } // 0 для обозначения сетевой ошибки
                }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}