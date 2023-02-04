package com.hfaria.ctw.topheadlines.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import com.hfaria.ctw.topheadlines.data.network.SuccessNetworkResponse
import com.hfaria.ctw.topheadlines.data.network.ThrowableNetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InMemoryTopHeadlinesPagingSource(
    private val api: TopHeadlinesApi,
    private val pageSize: Int
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val curPage = params.key ?: 1

            val response = withContext(Dispatchers.IO) {
                api.getTopHeadlines(
                    pageSize = pageSize,
                    page = curPage
                )
            }

            if (response is SuccessNetworkResponse<GetTopHeadlinesResponse>) {
                val articles = response.data.articles
                val nextPage = if (articles.size >= pageSize) curPage + 1 else null
                LoadResult.Page(
                    data = response.data.articles,
                    // Only paging forward
                    prevKey = null,
                    nextKey = nextPage
                )
            } else if (response is ThrowableNetworkResponse) {
                val msg = response.data.message.orEmpty()
                LoadResult.Error(response.data)
            } else {
                val msg = "UNKNOWN_ERROR"
                LoadResult.Error(Throwable(msg))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
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
