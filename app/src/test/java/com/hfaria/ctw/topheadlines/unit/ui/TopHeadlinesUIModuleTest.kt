package com.hfaria.ctw.topheadlines.unit.ui

import androidx.paging.PagingData
import com.hfaria.ctw.topheadlines.data.repository.TopHeadlinesRepository
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.ui.TopHeadlinesScreenState
import com.hfaria.ctw.topheadlines.ui.TopHeadlinesViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TopHeadlinesUIModuleTest {

    @Mock
    lateinit var state: TopHeadlinesScreenState

    @Mock
    lateinit var repo: TopHeadlinesRepository

    lateinit var vm: TopHeadlinesViewModel

    @Before
    fun setup() {
        vm = TopHeadlinesViewModel(state, repo)
    }

    @Test
    fun `On startup, should fetch Headline pages`() {
        // TODO
        //val article = Article(title = "FOOBAR")
        //vm.getTopHeadlines()
        //verify(state, times(1)).loadArticlePage(article)
    }

    @Test
    fun `On Headline click, should route to Headline Profile Screen`() {
        val article = Article(title = "FOOBAR")
        vm.onArticleClick(article)
        verify(state, times(1)).routeToHeadlineProfile(article)
    }
}