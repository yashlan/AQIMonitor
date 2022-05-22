package com.c22_ce02.awmonitorapp.api

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("current/airquality")
    fun getCurrentAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
    ) : Call<CurrentAirQualityResponse>

    @GET("current")
    fun getCurrentWeatherCondition(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
    ): Call<CurrentWeatherConditionResponse>

    @GET("forecast/airquality")
    fun getAirQualityForecastByHour(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
        @Query("hours") hours:Int,
    ): Call<AirQualityForecastByHourResponse>
    
}