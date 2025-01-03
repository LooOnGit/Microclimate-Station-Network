package com.example.loofarm.model

import kotlinx.serialization.Serializable

@Serializable
data class ThingSpeakResponse(
    val channel: Channel? = null,
    val feeds: List<Feed>? = null
)
