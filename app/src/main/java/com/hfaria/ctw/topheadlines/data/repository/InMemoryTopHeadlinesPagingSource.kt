package com.hfaria.ctw.topheadlines.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InMemoryTopHeadlinesPagingSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val api: TopHeadlinesApi,
    private val pageSize: Int
) : PagingSource<Int, Article>() {

    companion object {
        const val UNKNOWN_NETWORK_ERROR = "UNKNOWN_NETWORK_ERROR"
        const val API_ERROR = "API_ERROR"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val curPage = params.key ?: 1
            val response = callApi(ioDispatcher, curPage)
            parseNetworkResponse(curPage, response)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun parseNetworkResponse(curPage: Int, response: NetworkResponse<GetTopHeadlinesResponse>): LoadResult<Int, Article> {
        return if (response is SuccessNetworkResponse<GetTopHeadlinesResponse>) {
            parseApiResponse(curPage, response.data)
        } else if (response is ThrowableNetworkResponse) {
            LoadResult.Error(response.data)
        } else {
            LoadResult.Error(Throwable(UNKNOWN_NETWORK_ERROR))
        }
    }

    private fun parseApiResponse(curPage: Int, data: GetTopHeadlinesResponse): LoadResult<Int, Article> {
        return if (data.status != NewsApiStatus.OK) {
            LoadResult.Error(Throwable(API_ERROR))
        } else {
            val articles = data.articles
            val nextPage = if (articles.size >= pageSize) curPage + 1 else null
            LoadResult.Page(
                data = data.articles,
                // Only paging forward
                prevKey = null,
                nextKey = nextPage
            )
        }
    }

    private suspend fun callApi(dispatcher: CoroutineDispatcher, curPage: Int): NetworkResponse<GetTopHeadlinesResponse> {
        return withContext(dispatcher) {
            api.getTopHeadlines(
                pageSize = pageSize,
                page = curPage
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey:
        //      -  prevKey == null -> anchorPage is the first page.
        //      -  nextKey == null -> anchorPage is the last page.
        //      -  both prevKey and nextKey null -> anchorPage is the initial page, so
        //         just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
