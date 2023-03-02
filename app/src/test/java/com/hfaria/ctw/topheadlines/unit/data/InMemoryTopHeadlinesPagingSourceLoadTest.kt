package com.hfaria.ctw.topheadlines.unit.data

import androidx.paging.PagingSource
import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import com.hfaria.ctw.topheadlines.data.network.NetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.unit.mock.GetTopHeadlinesFakeResponses
import com.hfaria.ctw.topheadlines.unit.rules.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.withContext
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

    private suspend fun runPageLoadingTest(
        curPage: Int?,
        apiResponse: NetworkResponse<GetTopHeadlinesResponse>,
        expected: PagingSource.LoadResult.Page<Int, Article>
    ) {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(apiResponse)

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
        runPageLoadingTest(
            curPage = null,
            apiResponse = GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE,
            expected = PagingSource.LoadResult.Page(
                data = GetTopHeadlinesFakeResponses.ARTICLES,
                prevKey = null,
                nextKey = 2
            )
        )
    }

    @Test
    fun `Load middle page`() = runBlocking {
        runPageLoadingTest(
            curPage = 4,
            apiResponse = GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE,
            expected = PagingSource.LoadResult.Page(
                data = GetTopHeadlinesFakeResponses.ARTICLES,
                prevKey = null,
                nextKey = 5
            )
        )
    }

    @Test
    fun `Load last page`() = runBlocking {
        runPageLoadingTest(
            curPage = 4,
            apiResponse = GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE_LAST_PAGE,
            expected = PagingSource.LoadResult.Page(
                data = GetTopHeadlinesFakeResponses.ARTICLES_LAST_PAGE,
                prevKey = null,
                nextKey = null
            )
        )
    }

    @Test
    fun `Should handle NewsApiStatus ERROR response`() = runBlocking {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(GetTopHeadlinesFakeResponses.API_ERROR_RESPONSE)

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            Throwable(InMemoryTopHeadlinesPagingSource.API_ERROR)
        )

        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = GetTopHeadlinesFakeResponses.ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        Assert.assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Should handle API Exception`() = runBlocking {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).then { throw GetTopHeadlinesFakeResponses.EXCEPTION }

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            GetTopHeadlinesFakeResponses.EXCEPTION
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = GetTopHeadlinesFakeResponses.ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        Assert.assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Should handle ThrowableNetworkResponse`() = runBlocking {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(GetTopHeadlinesFakeResponses.THROWABLE_NETWORK_RESPONSE)

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            GetTopHeadlinesFakeResponses.EXCEPTION
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = GetTopHeadlinesFakeResponses.ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        Assert.assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Should handle any other NetworkResponse`() = runBlocking {
        Mockito.`when`(
            api.getTopHeadlines(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(GetTopHeadlinesFakeResponses.EMPTY_NETWORK_RESPONSE)

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            Throwable(InMemoryTopHeadlinesPagingSource.UNKNOWN_NETWORK_ERROR)
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = GetTopHeadlinesFakeResponses.ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        Assert.assertEquals(expected.toString(), actual.toString())
    }
}