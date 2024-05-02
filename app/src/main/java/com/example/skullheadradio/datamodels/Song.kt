package com.example.skullheadradio.datamodels

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ProgressBar
import java.util.Locale

data class Song(
    val title: String,
    val album: String?,
    val artist: Artist,
    val releaseyear:String?,
    val started_at: String,
    val ends_at: String
)