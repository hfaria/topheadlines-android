package com.hfaria.ctw.topheadlines.data.network

import com.google.gson.annotations.SerializedName

enum class NewsApiStatus {
    @SerializedName("ok")
    OK,
    @SerializedName("error")
    ERROR
}

data class NewsApiResponse(
    val status: NewsApiStatus,
    val code: String?,
    val message: String?
)