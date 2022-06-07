package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.AirQualityForecastAndHistoryByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.AirQualityForecastAndHistoryByHourRepository
import retrofit2.*

class AirQualityForecastAndHistoryByHourViewModel(private val repository: AirQualityForecastAndHistoryByHourRepository) :
    ViewModel() {

    fun getAirQualityForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String,
        onSuccess: (AirQualityForecastAndHistoryByHourResponse.Data?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getAirQualityForecastAndHistoryByHour(lat, lon, apiKey)
        call.enqueue(object : Callback<AirQualityForecastAndHistoryByHourResponse> {
            override fun onResponse(
                call: Call<AirQualityForecastAndHistoryByHourResponse>,
                response: Response<AirQualityForecastAndHistoryByHourResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    onError(response.errorBody().toString())
                }
            }

            override fun onFailure(
                call: Call<AirQualityForecastAndHistoryByHourResponse>,
                t: Throwable
            ) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}