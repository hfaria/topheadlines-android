package com.hfaria.ctw.topheadlines.unit.data

import androidx.paging.PagingSource
import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource.Companion.UNKNOWN_NETWORK_ERROR
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.ARTICLES
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.ARTICLES_LAST_PAGE
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.EMPTY_NETWORK_RESPONSE
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.EXCEPTION
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE_LAST_PAGE
import com.hfaria.ctw.topheadlines.unit.data.GetTopHeadlinesFakeResponses.THROWABLE_NETWORK_RESPONSE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

object GetTopHeadlinesFakeResponses {
    val ARTICLES = listOf(
        Article("A1"),
        Article("A2"),
        Article("A3"),
        Article("A4"),
        Article("A5"),
    )
    val ARTICLES_LAST_PAGE = ARTICLES.subList(0, ARTICLES.lastIndex - 2)

    val SUCCESS_RESPONSE = SuccessNetworkResponse(
        GetTopHeadlinesResponse(
            totalResults = ARTICLES.size,
            articles = ARTICLES
        )
    )
    val SUCCESS_RESPONSE_LAST_PAGE = SuccessNetworkResponse(
        GetTopHeadlinesResponse(
            totalResults = ARTICLES_LAST_PAGE.size,
            articles = ARTICLES_LAST_PAGE
        )
    )

    val EXCEPTION = Exception("GetTopHeadlinesFakeException")

    val THROWABLE_NETWORK_RESPONSE = ThrowableNetworkResponse<GetTopHeadlinesResponse>(
        EXCEPTION
    )

    val EMPTY_NETWORK_RESPONSE = EmptyNetworkResponse<GetTopHeadlinesResponse>()
}

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryTopHeadlinesPagingSourceTest {

    @Mock
    lateinit var api: TopHeadlinesApi

    lateinit var source: InMemoryTopHeadlinesPagingSource

    @Before
    fun setup() {
        source = InMemoryTopHeadlinesPagingSource(api, ARTICLES.size)
    }

    private suspend fun runPageLoadingTest(
        context: CoroutineContext,
        curPage: Int?,
        apiResponse: NetworkResponse<GetTopHeadlinesResponse>,
        expected: PagingSource.LoadResult.Page<Int, Article>
    ) {
        `when`(api.getTopHeadlines(
            anyString(),
            anyInt(),
            anyInt()
        )).thenReturn(apiResponse)

        val actual = withContext(context) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = curPage,
                    loadSize = ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Load first page`() = runBlocking {
        // First Page
        runPageLoadingTest(
            context = this.coroutineContext,
            curPage = null,
            apiResponse = SUCCESS_RESPONSE,
            expected = PagingSource.LoadResult.Page(
                data = ARTICLES,
                prevKey = null,
                nextKey = 2
            )
        )
    }

    @Test
    fun `Load middle page`() = runBlocking {
        runPageLoadingTest(
            context = this.coroutineContext,
            curPage = 4,
            apiResponse = SUCCESS_RESPONSE,
            expected = PagingSource.LoadResult.Page(
                data = ARTICLES,
                prevKey = null,
                nextKey = 5
            )
        )
    }

    @Test
    fun `Load last page`() = runBlocking {
        runPageLoadingTest(
            context = this.coroutineContext,
            curPage = 4,
            apiResponse = SUCCESS_RESPONSE_LAST_PAGE,
            expected = PagingSource.LoadResult.Page(
                data = ARTICLES_LAST_PAGE,
                prevKey = null,
                nextKey = null
            )
        )
    }

    @Test
    fun `Should handle API Exception`() = runBlocking {
        `when`(api.getTopHeadlines(
            anyString(),
            anyInt(),
            anyInt()
        )).then { throw EXCEPTION }

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            EXCEPTION
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Should handle ThrowableNetworkResponse`() = runBlocking {
        `when`(api.getTopHeadlines(
            anyString(),
            anyInt(),
            anyInt()
        )).thenReturn(THROWABLE_NETWORK_RESPONSE)

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            EXCEPTION
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Should handle any other NetworkResponse`() = runBlocking {
        `when`(api.getTopHeadlines(
            anyString(),
            anyInt(),
            anyInt()
        )).thenReturn(EMPTY_NETWORK_RESPONSE)

        val expected = PagingSource.LoadResult.Error<Int, Article>(
            Throwable(UNKNOWN_NETWORK_ERROR)
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = ARTICLES.size,
                    placeholdersEnabled = false
                )
            )
        }

        assertEquals(expected.toString(), actual.toString())
    }
}