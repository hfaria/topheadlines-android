package com.hfaria.ctw.topheadlines.data.repository.inDb

import androidx.paging.*
import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class TopHeadlinesMediator(
    private val ioDispatcher: CoroutineDispatcher,
    private val api: TopHeadlinesApi,
    private val pageSize: Int
) : RemoteMediator<Int, Article>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            val curPage = 1
            val response = callApi(ioDispatcher, curPage)
            MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            MediatorResult.Error(e)
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