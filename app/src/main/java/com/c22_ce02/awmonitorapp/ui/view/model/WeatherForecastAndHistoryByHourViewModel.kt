package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.WeatherForecastAndHistoryByHourRepository
import com.c22_ce02.awmonitorapp.data.response.WeatherHistoryAndForecastByHourResponse
import retrofit2.*

class WeatherForecastAndHistoryByHourViewModel : ViewModel() {
    fun getWeatherForecastAndHistoryByHour(
        onSuccess: (WeatherHistoryAndForecastByHourResponse.Data?) -> Unit,
    ) {
        val data = WeatherHistoryAndForecastByHourResponse.Data(
            listOf(
                WeatherHistoryAndForecastByHourResponse.History(
                    humidity = 76.7,
                    temperature = 35.4,
                    windSpeed = 54.5,
                ),
                WeatherHistoryAndForecastByHourResponse.History(
                    humidity = 78.7,
                    temperature = 25.4,
                    windSpeed = 24.5,
                ),
                WeatherHistoryAndForecastByHourResponse.History(
                    humidity = 56.7,
                    temperature = 33.4,
                    windSpeed = 29.5,
                ),
            ),
            listOf(
                WeatherHistoryAndForecastByHourResponse.Forecast(
                    humidity = 88.7,
                    temperature = 27.4,
                    windSpeed = 33.5,
                ),
                WeatherHistoryAndForecastByHourResponse.Forecast(
                    humidity = 84.7,
                    temperature = 21.4,
                    windSpeed = 35.5,
                ),
                WeatherHistoryAndForecastByHourResponse.Forecast(
                    humidity = 90.7,
                    temperature = 40.4,
                    windSpeed = 66.5,
                ),
            )
        )

        onSuccess.invoke(data)
    }
}