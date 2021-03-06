package com.c22_ce02.awmonitorapp.data.repository

import com.c22_ce02.awmonitorapp.api.ApiService

class ArticleRepository(private val apiService: ApiService) {
    fun getArticle() = apiService.getArticle()
}