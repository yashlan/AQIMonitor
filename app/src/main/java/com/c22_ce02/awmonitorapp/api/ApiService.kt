package com.c22_ce02.awmonitorapp.api

import com.c22_ce02.awmonitorapp.data.response.AirQualityForecastByHourResponse
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.response.CurrentWeatherConditionResponse
import com.c22_ce02.awmonitorapp.data.response.WeatherForecastByHourResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

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

    @GET("forecast/hourly")
    fun getWeatherForecastByHour(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
        @Query("hours") hours:Int,
    ): Call<WeatherForecastByHourResponse>
    
}