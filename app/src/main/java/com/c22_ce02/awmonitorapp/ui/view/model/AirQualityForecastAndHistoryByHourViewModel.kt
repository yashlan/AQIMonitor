package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.AirQualityForecastAndHistoryByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.AirQualityForecastAndHistoryByHourRepository
import retrofit2.*

class AirQualityForecastAndHistoryByHourViewModel(private val repository: AirQualityForecastAndHistoryByHourRepository) :
    ViewModel() {

    val listForecast = MutableLiveData<AirQualityForecastAndHistoryByHourResponse.Data?>()
    val errorMessage = MutableLiveData<String?>()

    fun getAirQualityForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String
    ) {
        listForecast.postValue(null)
        errorMessage.postValue(null)
        val call = repository.getAirQualityForecastAndHistoryByHour(lat, lon, apiKey)
        call.enqueue(object : Callback<AirQualityForecastAndHistoryByHourResponse> {
            override fun onResponse(
                call: Call<AirQualityForecastAndHistoryByHourResponse>,
                response: Response<AirQualityForecastAndHistoryByHourResponse>
            ) {
                if (response.isSuccessful) {
                    listForecast.postValue(response.body()?.data)
                } else {
                    errorMessage.postValue(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<AirQualityForecastAndHistoryByHourResponse>, t: Throwable) {
                errorMessage.postValue(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}