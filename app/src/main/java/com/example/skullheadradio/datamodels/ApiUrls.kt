package com.example.skullheadradio.datamodels

import com.google.gson.annotations.SerializedName

data class ApiUrls(
    val station: String,
    @SerializedName("current_song") val currentSong: String,
    @SerializedName("last_songs") val lastSongs: String,
    @SerializedName("next_artists") val nextArtists: String,
    val playlists: String,
    val schedule: String,
    val listeners: String
) {
}