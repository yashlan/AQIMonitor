package com.c22_ce02.awmonitorapp.api

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.model.CurrentAirQualityResponse
import com.c22_ce02.awmonitorapp.data.model.CurrentConditionResponse
import com.c22_ce02.awmonitorapp.data.model.FiveDaysOfDailyForecastResponse
import com.c22_ce02.awmonitorapp.data.model.GeoPositionResponse
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

    @GET
    fun getCurrentCondition(
        @Url url: String
    ): Call<List<CurrentConditionResponse>>

    @GET("locations/v1/cities/geoposition/search?apikey=${BuildConfig.API_KEY_ACCUWEATHER}&language=id")
    fun getInformationByGeoPosition(
        @Query("q") latLong: String
    ): Call<GeoPositionResponse>

    @GET
    fun get5DaysOfDailyForecasts(
        @Url url: String,
    ): Call<FiveDaysOfDailyForecastResponse>
}