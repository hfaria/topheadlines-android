package com.hfaria.ctw.topheadlines.ui

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cleanarch.codewars.demo.ui.base.BaseScreenState
import com.hfaria.ctw.topheadlines.data.repository.TopHeadlinesRepository
import com.hfaria.ctw.topheadlines.domain.Article
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class TopHeadlinesScreenState: BaseScreenState() {
    val articlePage: LiveData<PagingData<Article>>
        get() = _articlePage
    private val _articlePage = MutableLiveData<PagingData<Article>>()

    val articleProfileRoute: LiveData<Article>
        get() = _articleProfileRoute
    private val _articleProfileRoute = MutableLiveData<Article>()

    fun routeToArticleProfileScreen(article: Article) {
        _articleProfileRoute.value = article
    }

    fun loadArticlePage(page: PagingData<Article>) {
        _articlePage.value = page
    }
}

class TopHeadlinesViewModel @Inject constructor(
    private val topHeadlines: TopHeadlinesRepository
) : ViewModel() {

    val state = TopHeadlinesScreenState()

    fun getTopHeadlines() {
        viewModelScope.launch {
            topHeadlines.get()
                .cachedIn(viewModelScope)
                .collect(state::loadArticlePage)
        }
    }

    fun onArticleClick(article: Article) {
        state.routeToArticleProfileScreen(article)
    }
}
