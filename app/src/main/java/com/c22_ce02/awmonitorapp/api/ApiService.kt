package com.c22_ce02.awmonitorapp.api

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.data.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("current/airquality?key=${BuildConfig.API_KEY_WEATHERBIT_1}")
    fun getCurrentAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): Call<CurrentAirQualityResponse>

    @GET("current?key=${BuildConfig.API_KEY_WEATHERBIT_2}")
    fun getCurrentWeatherCondition(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): Call<CurrentWeatherConditionResponse>

    @GET("by_location?key=${BuildConfig.API_KEY_WEATHERBIT_3}")
    fun getAirQualityForecastAndHistoryByHour(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): Call<AirQualityForecastAndHistoryByHourResponse>

    @GET("forecast/hourly?key=${BuildConfig.API_KEY_WEATHERBIT_4}")
    fun getWeatherForecastByHour(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("hours") hours: Int,
    ): Call<WeatherForecastByHourResponse>

    @GET("2957848512475559337/posts?key=${BuildConfig.GOOGLE_API}&fetchImages=true")
    fun getArticle():Call<ArticleResponse>

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
    @POST("users/registrasi")
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
}