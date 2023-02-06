package com.hfaria.ctw.topheadlines.ui.top_headlines

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hfaria.ctw.topheadlines.android.util.SingleLiveEvent
import com.hfaria.ctw.topheadlines.ui.base.BaseScreenState
import com.hfaria.ctw.topheadlines.data.repository.TopHeadlinesRepository
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.ui.base.ErrorScreenState
import com.hfaria.ctw.topheadlines.ui.base.LoadingScreenState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

interface TopHeadlinesScreenState : ErrorScreenState, LoadingScreenState {
    val articlePage: LiveData<PagingData<Article>>
    val articleProfileRoute: LiveData<Article>

    fun loadArticlePage(page: PagingData<Article>)
    fun routeToHeadlineProfile(article: Article)
}

class TopHeadlinesScreenStateImpl : TopHeadlinesScreenState, BaseScreenState() {
    override val articlePage: LiveData<PagingData<Article>>
        get() = _articlePage
    private val _articlePage = MutableLiveData<PagingData<Article>>()

    override val articleProfileRoute: LiveData<Article>
        get() = _articleProfileRoute
    private val _articleProfileRoute = SingleLiveEvent<Article>()

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
                .catch { state.showErrorMessage(it) }
                .collect(state::loadArticlePage)
        }
    }

    fun onArticleClick(article: Article) {
        state.routeToHeadlineProfile(article)
    }
}
