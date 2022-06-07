package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.WeatherForecastByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.WeatherForecastByHourRepository
import retrofit2.*

class WeatherForecastByHourViewModel(private val repository: WeatherForecastByHourRepository) :
    ViewModel() {

    fun getWeatherForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String,
        hours: Int,
        onSuccess: (List<WeatherForecastByHourResponse.Data>?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getWeatherForecastByHour(lat, lon, apiKey, hours)
        call.enqueue(object : Callback<WeatherForecastByHourResponse> {
            override fun onResponse(
                call: Call<WeatherForecastByHourResponse>,
                response: Response<WeatherForecastByHourResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    onError(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<WeatherForecastByHourResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}