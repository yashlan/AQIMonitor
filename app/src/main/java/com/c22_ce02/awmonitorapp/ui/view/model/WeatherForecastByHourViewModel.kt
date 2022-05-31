package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.WeatherForecastByHourResponse
import com.c22_ce02.awmonitorapp.data.repository.WeatherForecastByHourRepository
import retrofit2.*

class WeatherForecastByHourViewModel(private val repository: WeatherForecastByHourRepository) :
    ViewModel() {

    val listForecast = MutableLiveData<List<WeatherForecastByHourResponse.Data>?>()
    val errorMessage = MutableLiveData<String?>()

    fun getWeatherForecastByHour(
        lat: Double,
        lon: Double,
        apiKey: String,
        hours: Int
    ) {
        listForecast.postValue(null)
        errorMessage.postValue(null)
        val call = repository.getWeatherForecastByHour(lat, lon, apiKey, hours)
        call.enqueue(object : Callback<WeatherForecastByHourResponse> {
            override fun onResponse(
                call: Call<WeatherForecastByHourResponse>,
                response: Response<WeatherForecastByHourResponse>
            ) {
                if (response.isSuccessful) {
                    listForecast.postValue(response.body()?.data)
                } else {
                    errorMessage.postValue(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<WeatherForecastByHourResponse>, t: Throwable) {
                errorMessage.postValue(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}