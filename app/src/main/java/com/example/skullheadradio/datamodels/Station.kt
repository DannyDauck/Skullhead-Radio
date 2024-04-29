package com.example.skullheadradio.datamodels

import com.google.gson.annotations.SerializedName

data class Station(

    @SerializedName("display_name") val displayName: String,
    @SerializedName("spoken_name") val spokenName: String,
    @SerializedName("page_url") val pageUrl: String,
    @SerializedName("stream_url") val streamUrl: String,
    @SerializedName("api_urls") val apiUrls: ApiUrls,
    val format: String,
    val description: String,
    val djs: String,
    val location: String,
    val lat: Double,
    val lng: Double,
    val color: String,
    @SerializedName("updated_at") val updatedAt: String,
    val genres: List<String>,
    val active: Boolean,
    val position: Int,

    )
