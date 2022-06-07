package com.c22_ce02.awmonitorapp.di

import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.api.ApiConfig
import com.c22_ce02.awmonitorapp.data.repository.*

object Injection {

    fun provideCurrentWeatherConditionRepository(): CurrentWeatherConditionRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_WEATHERBIT)
        return CurrentWeatherConditionRepository(apiService)
    }

    fun provideCurrentAirQualityRepository(): CurrentAirQualityRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_WEATHERBIT)
        return CurrentAirQualityRepository(apiService)
    }

    fun provideAirQualityForecastAndHistoryByHourRepository(): AirQualityForecastAndHistoryByHourRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_AQIMonitor_GET)
        return AirQualityForecastAndHistoryByHourRepository(apiService)
    }

    fun provideWeatherForecastByHourRepository(): WeatherForecastByHourRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_WEATHERBIT)
        return WeatherForecastByHourRepository(apiService)
    }

    fun providePostCurrentWeatherAndAirDataRepository(): PostCurrentWeatherAndAirDataRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_AQIMonitor_POST)
        return PostCurrentWeatherAndAirDataRepository(apiService)
    }

    fun provideRegisterRepository(): RegisterRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_AQIMonitor_POST)
        return RegisterRepository(apiService)
    }

    fun provideLoginRepository(): LoginRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_AQIMonitor_POST)
        return LoginRepository(apiService)
    }

    fun provideArticleRepository(): ArticleRepository {
        val apiService = ApiConfig.getApiService(BuildConfig.BASE_URL_BLOGGER)
        return ArticleRepository(apiService)
    }
}