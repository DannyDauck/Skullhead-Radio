package com.example.skullheadradio.datamodels

import com.google.gson.annotations.SerializedName

data class Artist(
    val name: String,
    val image: String,
    @SerializedName("laut_url") val lautURL: String
)