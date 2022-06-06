package com.c22_ce02.awmonitorapp.api

import com.c22_ce02.awmonitorapp.data.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("current/airquality")
    fun getCurrentAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
    ): Call<CurrentAirQualityResponse>

    @GET("current")
    fun getCurrentWeatherCondition(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
    ): Call<CurrentWeatherConditionResponse>

    @GET("by_location")
    fun getAirQualityForecastAndHistoryByHour(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
    ): Call<AirQualityForecastAndHistoryByHourResponse>

    @GET("forecast/hourly")
    fun getWeatherForecastByHour(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
        @Query("hours") hours: Int,
    ): Call<WeatherForecastByHourResponse>

    @FormUrlEncoded
    @POST("history/input")
    fun postCurrentWeatherAndAirData(
        @Field("location") location: String,
        @Field("date") date: String,
        @Field("aqi") aqi: Double,
        @Field("o3") o3: Double,
        @Field("so2") so2: Double,
        @Field("no2") no2: Double,
        @Field("co") co: Double,
        @Field("pm10") pm10: Double,
        @Field("pm25") pm25: Double,
        @Field("temperature") temperature: Double,
        @Field("humidity") humidity: Double,
        @Field("wind_speed") windSpeed: Double,
    ): Call<PostCurrentWeatherAndAirResponse>

    @FormUrlEncoded
    @POST("users/createaccount")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("2957848512475559337/posts")
    fun getArticle(
        @Query("key") key: String,
        @Query("fetchImages") fetchImages: Boolean
    ):Call<ArticleResponse>
}