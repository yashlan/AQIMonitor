package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.AirQualityForecastByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.AirQualityForecastByHourRepository
import retrofit2.*

class AirQualityForecastByHourViewModel(private val repository: AirQualityForecastByHourRepository) :
    ViewModel() {

    val listForecast = MutableLiveData<List<AirQualityForecastByHourResponse.Data>?>()
    val errorMessage = MutableLiveData<String?>()

    fun getAirQualityForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String,
        hours: Int
    ) {
        listForecast.postValue(null)
        errorMessage.postValue(null)
        val call = repository.getAirQualityForecastByHour(lat, lon, apiKey, hours)
        call.enqueue(object : Callback<AirQualityForecastByHourResponse> {
            override fun onResponse(
                call: Call<AirQualityForecastByHourResponse>,
                response: Response<AirQualityForecastByHourResponse>
            ) {
                if (response.isSuccessful) {
                    listForecast.postValue(response.body()?.data)
                } else {
                    errorMessage.postValue(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<AirQualityForecastByHourResponse>, t: Throwable) {
                errorMessage.postValue(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}