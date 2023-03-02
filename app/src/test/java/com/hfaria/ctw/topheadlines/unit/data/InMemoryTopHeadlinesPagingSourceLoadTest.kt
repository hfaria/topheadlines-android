package com.hfaria.ctw.topheadlines.unit.data

import androidx.paging.PagingSource
import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import com.hfaria.ctw.topheadlines.data.network.NetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.unit.mock.GetTopHeadlinesFakeResponses
import com.hfaria.ctw.topheadlines.unit.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryTopHeadlinesPagingSourceLoadTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var api: TopHeadlinesApi

    lateinit var source: InMemoryTopHeadlinesPagingSource

    @Before
    fun setup() {
        source = InMemoryTopHeadlinesPagingSource(
            mainDispatcherRule.testDispatcher,
            api,
            GetTopHeadlinesFakeResponses.ARTICLES.size
        )
    }

    private fun givenApiResponse(apiResponse: NetworkResponse<GetTopHeadlinesResponse>) {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(apiResponse)
    }

    private fun givenApiException(t: Throwable) {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).then { throw t }
    }

    private suspend fun runPageLoadingTest(
        curPage: Int?,
        expected: PagingSource.LoadResult<Int, Article>
    ) {
        val actual = source.load(
            PagingSource.LoadParams.Refresh(
                key = curPage,
                loadSize = GetTopHeadlinesFakeResponses.ARTICLES.size,
                placeholdersEnabled = false
            )
        )

        Assert.assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Load first page`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE)
        runPageLoadingTest(
            curPage = null,
            expected = PagingSource.LoadResult.Page(
                data = GetTopHeadlinesFakeResponses.ARTICLES,
                prevKey = null,
                nextKey = 2
            )
        )
    }

    @Test
    fun `Load middle page`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE)
        runPageLoadingTest(
            curPage = 4,
            expected = PagingSource.LoadResult.Page(
                data = GetTopHeadlinesFakeResponses.ARTICLES,
                prevKey = null,
                nextKey = 5
            )
        )
    }

    @Test
    fun `Load last page`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE_LAST_PAGE)
        runPageLoadingTest(
            curPage = 4,
            expected = PagingSource.LoadResult.Page(
                data = GetTopHeadlinesFakeResponses.ARTICLES_LAST_PAGE,
                prevKey = null,
                nextKey = null
            )
        )
    }

    @Test
    fun `Should handle NewsApiStatus ERROR response`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.API_ERROR_RESPONSE)
        runPageLoadingTest(
            curPage = 1,
            expected = PagingSource.LoadResult.Error(
                Throwable(InMemoryTopHeadlinesPagingSource.API_ERROR)
            )
        )
    }

    @Test
    fun `Should handle ThrowableNetworkResponse`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.THROWABLE_NETWORK_RESPONSE)
        runPageLoadingTest(
            curPage = 1,
            expected = PagingSource.LoadResult.Error(
                GetTopHeadlinesFakeResponses.EXCEPTION
            )
        )
    }

    @Test
    fun `Should handle any other NetworkResponse`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.EMPTY_NETWORK_RESPONSE)
        runPageLoadingTest(
            curPage = 1,
            expected = PagingSource.LoadResult.Error(
                Throwable(InMemoryTopHeadlinesPagingSource.UNKNOWN_NETWORK_ERROR)
            )
        )
    }

    @Test
    fun `Should handle API Exception`() = runBlocking {
        givenApiException(GetTopHeadlinesFakeResponses.EXCEPTION)
        runPageLoadingTest(
            curPage = 1,
            expected = PagingSource.LoadResult.Error(
                GetTopHeadlinesFakeResponses.EXCEPTION
            )
        )
    }
}
