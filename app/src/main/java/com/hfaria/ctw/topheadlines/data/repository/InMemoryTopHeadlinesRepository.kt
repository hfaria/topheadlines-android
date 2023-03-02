package com.hfaria.ctw.topheadlines.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class InMemoryTopHeadlinesRepository @Inject constructor(
    private val topHeadlinesApi: TopHeadlinesApi,
    private val ioDispatcher: CoroutineDispatcher,
    private val pageSize: Int
): TopHeadlinesRepository {

    override fun get(): Flow<PagingData<Article>> = Pager (
        PagingConfig(pageSize)
    ) {
        InMemoryTopHeadlinesPagingSource(ioDispatcher, topHeadlinesApi, pageSize)
    }.flow
}
