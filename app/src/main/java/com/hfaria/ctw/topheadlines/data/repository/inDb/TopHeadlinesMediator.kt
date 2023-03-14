package com.hfaria.ctw.topheadlines.data.repository.inDb

import androidx.paging.*
import com.hfaria.ctw.topheadlines.data.db.TopHeadlinesDbRepository
import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class TopHeadlinesMediator(
    private val ioDispatcher: CoroutineDispatcher,
    private val api: TopHeadlinesApi,
    val dbRepository: TopHeadlinesDbRepository,
    private val pageSize: Int
) : RemoteMediator<Int, Article>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            val curPage = 1
            val response = callApi(ioDispatcher, curPage)
            parseNetworkResponse(curPage, response)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun parseNetworkResponse(curPage: Int, response: NetworkResponse<GetTopHeadlinesResponse>): MediatorResult {
        return when (response) {
            is SuccessNetworkResponse<GetTopHeadlinesResponse> -> {
                parseApiResponse(curPage, response.data)
            }
            is ThrowableNetworkResponse -> {
                MediatorResult.Error(response.data)
            }
            else -> {
                MediatorResult.Error(Throwable(InMemoryTopHeadlinesPagingSource.UNKNOWN_NETWORK_ERROR))
            }
        }
    }

    private fun parseApiResponse(curPage: Int, data: GetTopHeadlinesResponse): MediatorResult {
        return if (data.status != NewsApiStatus.OK) {
            MediatorResult.Error(Throwable(InMemoryTopHeadlinesPagingSource.API_ERROR))
        } else {
            val articles = data.articles
            dbRepository.insertAll(articles)
            MediatorResult.Success(endOfPaginationReached = false)
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
}