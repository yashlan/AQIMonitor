package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.AirQualityForecastByHourRepository
import retrofit2.*

class AirQualityForecastByHourViewModel(private val repository: AirQualityForecastByHourRepository) :
    ViewModel() {
    fun getAirQualityForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String,
        hours: Int,
        onSuccess: (List<AirQualityForecastByHourResponse.Data>?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        val call = repository.getAirQualityForecastByHour(lat, lon, apiKey, hours)
        call.enqueue(object : Callback<AirQualityForecastByHourResponse> {
            override fun onResponse(
                call: Call<AirQualityForecastByHourResponse>,
                response: Response<AirQualityForecastByHourResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    onFailed(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<AirQualityForecastByHourResponse>, t: Throwable) {
                onFailed(t.message.toString())
            }
        })
    }
}