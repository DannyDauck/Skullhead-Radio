package com.example.skullheadradio.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
//TODO ganze Datei löschen nach Präsentation
const val BASE_URL = "https://api.laut.fm"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface LautFmApiService {

    @GET("/station/{name}")
    suspend fun getStation(name: String): Station

    @GET("/genres")
    suspend fun getAllGenres(): List<Genre>

    @GET("/station/{name}/current_song")
    suspend fun getCurrentSong(station: String): LiveData<Song>

    @GET("/stations/genre/{genre}")
    suspend fun getStationsByGenre(genre: String): List<Station>


    object LautFmApi {
        val retrofitService: LautFmApiService by lazy { retrofit.create(LautFmApiService::class.java) }
    }

}