package com.example.loofarm.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("entry_id")
    val entryId: Int? = null,
    val field1: String? = null,
    val field2: String? = null,
    val field3: String? = null,
    val field4: String? = null,
    val field5: String? = null,
    val field6: String? = null,
    val field7: String? = null
)
