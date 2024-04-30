package com.example.skullheadradio.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skullheadradio.datamodels.Genre
import com.example.skullheadradio.datamodels.Song
import com.example.skullheadradio.datamodels.Station
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun getStationsByGenre(
        @Path("genre") genre: String,
        @Query("order") order: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Header("If-Modified-Since") ifModifiedSince: String? = null
    ): Response<List<Station>>

    @GET("/station/{station}/images/{type}")
    suspend fun getImage(station: String, type: String = "station"): String



    object LautFmApi {
        val retrofitService: LautFmApiService by lazy { retrofit.create(LautFmApiService::class.java) }
    }

}