package com.example.loofarm.model

object ThingSpeakManager {
    var channelId: Long = 0L
    var channelIdAI: Long = 2680054
    var apiKey: String = ""

    const val SENSOR_KEY = "Sensor"
    const val SENSOR_TEMP = 1
    const val SENSOR_HUM = 2
    const val SENSOR_SAL = 3
    const val SENSOR_FLOW = 4

    const val AI_KEY = "AI"
    const val FORECAST_KEY = "AI forecast"
}
