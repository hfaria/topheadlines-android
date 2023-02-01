package com.hfaria.ctw.topheadlines.data.network

import com.google.gson.annotations.SerializedName
import com.hfaria.ctw.topheadlines.domain.Article

enum class NewsApiStatus {
    @SerializedName("ok")
    OK,
    @SerializedName("error")
    ERROR,
    DEFAULT
}

open class NewsApiResponse(
    val status: NewsApiStatus = NewsApiStatus.DEFAULT,
    val code: String? = null,
    val message: String? = null
)

data class GetTopHeadlinesResponse(
    val totalResults: Int,
    val articles: List<Article> = listOf()
) : NewsApiResponse()
