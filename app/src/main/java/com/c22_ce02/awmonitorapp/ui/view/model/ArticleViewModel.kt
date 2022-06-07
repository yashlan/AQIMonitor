package com.c22_ce02.awmonitorapp.ui.view.model

import androidx.lifecycle.ViewModel
import com.c22_ce02.awmonitorapp.data.response.ArticleResponse
import com.c22_ce02.awmonitorapp.data.repository.ArticleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {

    fun getArticle(
        key: String,
        fetchImage: Boolean,
        onSuccess: (List<ArticleResponse.ItemsItem>?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val call = repository.getArticle(key, fetchImage)
        call.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                if (response.isSuccessful)
                    onSuccess(response.body()?.items)
                else
                    onError(response.errorBody().toString())
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                onError(t.localizedMessage?.toString() ?: t.message.toString())
            }
        })
    }
}