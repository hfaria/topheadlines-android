package com.hfaria.ctw.topheadlines.domain

data class Article(
    val title: String,
    val description: String = "",
    val publishedAt: String = "",
    val author: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
    val content: String? = null,
)