package com.hfaria.ctw.topheadlines.data.db

import com.hfaria.ctw.topheadlines.domain.Article

interface TopHeadlinesDbRepository {

    fun insertAll(articles: List<Article>)
}