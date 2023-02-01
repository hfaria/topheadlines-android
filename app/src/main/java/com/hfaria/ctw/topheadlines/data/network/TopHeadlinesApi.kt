package com.hfaria.ctw.topheadlines.data.network

import com.hfaria.ctw.topheadlines.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface TopHeadlinesApi {

    @GET("top-headlines")
    fun getTopHeadlines(
        //@Query("sources")  sources:  String = BuildConfig.NEWS_SOURCE,
        @Query("country")  country : String = "us",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page")     page    : Int = 1,
    ): NetworkResponse<GetTopHeadlinesResponse>

}