package com.hfaria.ctw.topheadlines.unit.mock

import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import okhttp3.ResponseBody
import retrofit2.Response

object RetrofitFakeResponses {

    val SUCCESS_RESPONSE = Response.success(GetTopHeadlinesResponse())

    val EMPTY_RESPONSE = Response.success<GetTopHeadlinesResponse>(null)

    val NOT_FOUND_RESPONSE = Response.error<GetTopHeadlinesResponse>(
        404,
        ResponseBody.create(null, "")
    )

    val GENERIC_ERROR = "GENERIC_ERROR"
    val GENERIC_ERROR_RESPONSE = Response.error<GetTopHeadlinesResponse>(
        425,
        ResponseBody.create(null, GENERIC_ERROR)
    )

    val EXCEPTION = Exception("RetrofitFakeException")
}