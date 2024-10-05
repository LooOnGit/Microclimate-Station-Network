package com.example.loofarm.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//http://localhost:8081/api/v1/users
/**
 * B1: Đảm bảo Mobile và Laptop phải cùng connect đến cùng một mạng (wifi hoặc phát dữ liệu di động cho máy tính bắt)
 * B2: ipconfig > lấy địa chỉ ipV4 thay vào localhost
 * VD -> 192.168.1.44
 *
 */
private const val BASE_URL = "https://api.thingspeak.com/"

val logging = HttpLoggingInterceptor().apply {
    setLevel(HttpLoggingInterceptor.Level.BODY)
}

val httpClient = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
 */
private val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(httpClient)
    .build()

object LooFarmApi {
    val retrofitService: LooFarmService by lazy {
        retrofit.create(LooFarmService::class.java)
    }
}
