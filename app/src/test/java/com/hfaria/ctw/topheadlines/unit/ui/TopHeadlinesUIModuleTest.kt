package com.hfaria.ctw.topheadlines.unit.ui

import androidx.paging.PagingData
import com.hfaria.ctw.topheadlines.data.repository.TopHeadlinesRepository
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.ui.TopHeadlinesScreenState
import com.hfaria.ctw.topheadlines.ui.TopHeadlinesViewModel
import com.hfaria.ctw.topheadlines.unit.base.BaseCoroutineTest
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

@RunWith(MockitoJUnitRunner::class)
class TopHeadlinesUIModuleTest : BaseCoroutineTest() {

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
        val article = Article(title = "FOOBAR")
        val page = PagingData.from(listOf(article))
        val flow = flowOf(page)
        `when`(repo.get()).thenReturn(flow)
        vm.getTopHeadlines()

        /*
            We need to use "any()" matcher here because Flow creates
            a new instance of emitted objects for each subscriber
            to prevent any race conditions.
        */
        verify(state, times(1)).loadArticlePage(any(page.javaClass))
    }

    @Test
    fun `On Headline click, should route to Headline Profile Screen`() {
        val article = Article(title = "FOOBAR")
        vm.onArticleClick(article)
        verify(state, times(1)).routeToHeadlineProfile(article)
    }
}