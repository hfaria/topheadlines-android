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

interface TopHeadlinesScreenState {
    fun loadArticlePage(page: PagingData<Article>)
    fun routeToHeadlineProfile(article: Article)
}


class TopHeadlinesScreenStateImpl : TopHeadlinesScreenState, BaseScreenState() {
    val articlePage: LiveData<PagingData<Article>>
        get() = _articlePage
    private val _articlePage = MutableLiveData<PagingData<Article>>()

    val articleProfileRoute: LiveData<Article>
        get() = _articleProfileRoute
    private val _articleProfileRoute = MutableLiveData<Article>()

    override fun routeToHeadlineProfile(article: Article) {
        _articleProfileRoute.value = article
    }

    override fun loadArticlePage(page: PagingData<Article>) {
        _articlePage.value = page
    }
}

class TopHeadlinesViewModel @Inject constructor(
    val state: TopHeadlinesScreenState = TopHeadlinesScreenStateImpl(),
    private val topHeadlines: TopHeadlinesRepository
) : ViewModel() {


    fun getTopHeadlines() {
        viewModelScope.launch {
            topHeadlines.get()
                .cachedIn(viewModelScope)
                .collect(state::loadArticlePage)
        }
    }

    fun onArticleClick(article: Article) {
        state.routeToHeadlineProfile(article)
    }
}
