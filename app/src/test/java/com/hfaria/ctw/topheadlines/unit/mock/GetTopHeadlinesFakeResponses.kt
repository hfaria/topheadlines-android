package com.hfaria.ctw.topheadlines.unit.mock

import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.domain.Article

object GetTopHeadlinesFakeResponses {
    val ARTICLES = listOf(
        Article("A1"),
        Article("A2"),
        Article("A3"),
        Article("A4"),
        Article("A5"),
    )
    val ARTICLES_LAST_PAGE = ARTICLES.subList(0, ARTICLES.lastIndex - 2)

    val SUCCESS_RESPONSE = SuccessNetworkResponse(
        GetTopHeadlinesResponse(
            totalResults = ARTICLES.size,
            articles = ARTICLES
        )
    )

    val SUCCESS_RESPONSE_LAST_PAGE = SuccessNetworkResponse(
        GetTopHeadlinesResponse(
            totalResults = ARTICLES_LAST_PAGE.size,
            articles = ARTICLES_LAST_PAGE
        )
    )

    val API_ERROR_RESPONSE = SuccessNetworkResponse(
        GetTopHeadlinesResponse(
            status = NewsApiStatus.ERROR,
            code = "225",
            message = "FakeError"
        )
    )

    val EXCEPTION = Exception("GetTopHeadlinesFakeException")

    val THROWABLE_NETWORK_RESPONSE = ThrowableNetworkResponse<GetTopHeadlinesResponse>(
        EXCEPTION
    )

    val EMPTY_NETWORK_RESPONSE = EmptyNetworkResponse<GetTopHeadlinesResponse>()
}