package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.repository.MapsRepository
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQuality34ProvinceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val repository: MapsRepository) : ViewModel() {

    fun getCurrentAirQuality34Province(
        onSuccess: (List<CurrentAirQuality34ProvinceResponse.CurrentItem>?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getCurrentAirQuality34Province()
        call.enqueue(object : Callback<CurrentAirQuality34ProvinceResponse> {
            override fun onResponse(
                call: Call<CurrentAirQuality34ProvinceResponse>,
                response: Response<CurrentAirQuality34ProvinceResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data?.current)
                } else {
                    onError("Terjadi Kesalahan")
                }
            }

            override fun onFailure(call: Call<CurrentAirQuality34ProvinceResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}