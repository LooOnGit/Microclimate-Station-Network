package com.example.loofarm.network

import com.example.loofarm.model.ThingSpeakResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service object for creating api calls
 */
interface LooFarmService {
    @GET("channels/{channelId}/feeds.json")
    suspend fun getFeeds(
        @Path("channelId") channelId: Long,
        @Query("api_key") apiKey: String,
        @Query("results") results: Int
    ): ThingSpeakResponse

    @GET("channels/{channelId}/feeds.json")
    suspend fun getFeedsAI(
        @Path("channelId") channelId: Long,
        @Query("results") results: Int
    ): ThingSpeakResponse
}
