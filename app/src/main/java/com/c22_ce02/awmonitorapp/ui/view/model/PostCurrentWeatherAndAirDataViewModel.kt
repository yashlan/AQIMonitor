package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.PostCurrentWeatherAndAirDataRepository
import com.c22_ce02.awmonitorapp.data.response.PostCurrentWeatherAndAirResponse
import com.c22_ce02.awmonitorapp.utils.translateError
import com.google.gson.Gson
import retrofit2.*

class PostCurrentWeatherAndAirDataViewModel(private val repository: PostCurrentWeatherAndAirDataRepository) :
    ViewModel() {

    fun postCurrentWeatherAndAirData(
        location: String,
        date: String,
        aqi: Double,
        o3: Double,
        so2: Double,
        no2: Double,
        co: Double,
        pm10: Double,
        pm25: Double,
        humidity: Double,
        temperature: Double,
        windSpeed: Double,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.postCurrentWeatherAndAirData(
            location = location,
            date = date,
            aqi = aqi,
            o3 = o3,
            so2 = so2,
            no2 = no2,
            co = co,
            pm10 = pm10,
            pm25 = pm25,
            humidity = humidity,
            temperature = temperature,
            windSpeed = windSpeed,
        )

        call.enqueue(object : Callback<PostCurrentWeatherAndAirResponse> {
            override fun onResponse(
                call: Call<PostCurrentWeatherAndAirResponse>,
                response: Response<PostCurrentWeatherAndAirResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess("post data current weather and air berhasil!")
                } else {
                    onError(response.translateError())
                }
            }

            override fun onFailure(call: Call<PostCurrentWeatherAndAirResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}