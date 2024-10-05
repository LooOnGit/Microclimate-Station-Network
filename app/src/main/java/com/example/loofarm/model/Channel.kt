package com.example.loofarm.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: Int? = null,
    val name: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val field1: String? = null,
    val field2: String? = null,
    val field3: String? = null,
    val field4: String? = null,
    val field5: String? = null,
    val field6: String? = null,
    val field7: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("last_entry_id")
    val lastEntryId: Int? = null
)
