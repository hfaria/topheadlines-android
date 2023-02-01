package com.hfaria.ctw.topheadlines.data.repository

import androidx.paging.PagingData
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.flow.Flow

interface TopHeadlinesRepository {
    fun get(): Flow<PagingData<Article>>
}