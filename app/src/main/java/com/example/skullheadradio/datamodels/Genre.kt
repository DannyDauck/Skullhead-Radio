package com.example.skullheadradio.datamodels

data class Genre(
    val name: String,
    val score: Int,
    val related: List<String>
)