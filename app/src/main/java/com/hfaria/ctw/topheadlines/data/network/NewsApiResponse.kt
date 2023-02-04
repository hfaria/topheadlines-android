package com.hfaria.ctw.topheadlines.data.network

import com.google.gson.annotations.SerializedName
import com.hfaria.ctw.topheadlines.domain.Article

enum class NewsApiStatus {
    @SerializedName("ok")
    OK,
    @SerializedName("error")
    ERROR,
}

data class GetTopHeadlinesResponse(
    val status: NewsApiStatus = NewsApiStatus.OK,
    val code: String? = null,
    val message: String? = null,
    val totalResults: Int = -1,
    val articles: List<Article> = listOf()
)
