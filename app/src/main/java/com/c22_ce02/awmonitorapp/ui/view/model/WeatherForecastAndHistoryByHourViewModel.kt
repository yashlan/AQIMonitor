package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.WeatherForecastAndHistoryByHourRepository
import com.c22_ce02.awmonitorapp.data.response.WeatherHistoryAndForecastByHourResponse
import retrofit2.*

class WeatherForecastAndHistoryByHourViewModel(private val repository: WeatherForecastAndHistoryByHourRepository) :
    ViewModel() {
    fun getWeatherForecastAndHistoryByHour(
        lat: Double,
        lon: Double,
        onSuccess: (WeatherHistoryAndForecastByHourResponse.Data?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getWeatherForecastAndHistoryByHour(lat, lon)
        call.enqueue(object : Callback<WeatherHistoryAndForecastByHourResponse> {
            override fun onResponse(
                call: Call<WeatherHistoryAndForecastByHourResponse>,
                response: Response<WeatherHistoryAndForecastByHourResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    onError("Terjadi Kesalahan")
                }
            }

            override fun onFailure(
                call: Call<WeatherHistoryAndForecastByHourResponse>,
                t: Throwable
            ) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}