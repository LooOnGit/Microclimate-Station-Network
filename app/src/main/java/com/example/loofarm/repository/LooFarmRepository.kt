package com.example.loofarm.repository

import com.example.loofarm.model.ThingSpeakResponse
import com.example.loofarm.network.LooFarmApi

/**
 * Repository sẽ chịu trách nhiệm lấy dữ liệu từ API thông qua Retrofit và chuyển dữ liệu đó cho ViewModel
 */
class LooFarmRepository {
    suspend fun getThingSpeakData(
        channelId: Long,
        apiKey: String,
        results: Int = 1
    ): ThingSpeakResponse {
        return LooFarmApi.retrofitService.getFeeds(
            channelId = channelId,
            apiKey = apiKey,
            results = results
        )
    }

    suspend fun getThingSpeakDataAI(
        channelId: Long,
        results: Int = 1
    ): ThingSpeakResponse {
        return LooFarmApi.retrofitService.getFeedsAI(
            channelId = channelId,
            results = results
        )
    }
}
