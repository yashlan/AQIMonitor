package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.model.GeoPositionResponse
import com.c22_ce02.awmonitorapp.data.repository.GeoPositionRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeoPositionViewModel(private val repository: GeoPositionRepository) : ViewModel() {
    fun getInformationUserByGeoPosition(
        latLong: String,
        onSuccess: (GeoPositionResponse?) -> Unit,
        onFailed: (String?) -> Unit
    ) {
        val call = repository.getInformationUserByGeoPosition(latLong)
        call.enqueue(object: Callback<GeoPositionResponse> {
            override fun onResponse(
                call: Call<GeoPositionResponse>,
                response: Response<GeoPositionResponse>
            ) {
                if(response.isSuccessful) {
                    onSuccess(response.body())
                }else{
                    onFailed(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<GeoPositionResponse>, t: Throwable) {
                onFailed(t.message.toString())
            }
        })
    }
}