package com.example.loofarm.repository

import com.example.loofarm.model.ThingSpeakResponse
import com.example.loofarm.network.LooFarmApi

/**
 * Repository sẽ chịu trách nhiệm lấy dữ liệu từ API thông qua Retrofit và chuyển dữ liệu đó cho ViewModel
 */
class LooFarmRepository {
    suspend fun getFeeds(channelId: Long, apiKey: String): ThingSpeakResponse {
        return LooFarmApi.retrofitService.getFeeds(channelId = channelId, apiKey = apiKey)
    }
}
