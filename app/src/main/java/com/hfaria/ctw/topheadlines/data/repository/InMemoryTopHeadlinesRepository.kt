package com.hfaria.ctw.topheadlines.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InMemoryTopHeadlinesRepository @Inject constructor(
    private val topHeadlinesApi: TopHeadlinesApi,
    private val pageSize: Int = 20
): TopHeadlinesRepository {

    override fun get(): Flow<PagingData<Article>> = Pager (
        PagingConfig(pageSize)
    ) {
        InMemoryTopHeadlinesPagingSource(topHeadlinesApi, pageSize)
    }.flow
}
