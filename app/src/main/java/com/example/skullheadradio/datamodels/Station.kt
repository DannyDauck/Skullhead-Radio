package com.example.skullheadradio.datamodels

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET as GET

data class Station(
    val name: String,
    val display_name: String,
    val format: String,
    val description: String,
    val current_playlist: Playlist?,
    val djs: String,
    val location: String,
    val lat: Double?,
    val lng: Double?,
    val images: StationImages,
    val genres: List<String>,
    val active: Boolean,
    val position: Int,

    )
