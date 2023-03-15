package com.hfaria.ctw.topheadlines.unit.data.inDb

import androidx.paging.*
import com.hfaria.ctw.topheadlines.data.db.TopHeadlinesDbRepository
import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import com.hfaria.ctw.topheadlines.data.network.NetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.data.repository.inDb.TopHeadlinesMediator
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.unit.mock.GetTopHeadlinesFakeResponses
import com.hfaria.ctw.topheadlines.unit.rules.MainDispatcherRule
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@OptIn(ExperimentalPagingApi::class)
@RunWith(MockitoJUnitRunner::class)
class TopHeadlinesMediatorLoadTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var api: TopHeadlinesApi

    @Mock
    lateinit var dbRepository: TopHeadlinesDbRepository

    @Mock
    lateinit var state: PagingState<Int, Article>

    lateinit var mediator: TopHeadlinesMediator

    @Before
    fun setup() {
        mediator = TopHeadlinesMediator(
            mainDispatcherRule.testDispatcher,
            api,
            dbRepository,
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

    private fun givenDbException(t: Throwable) {
        Mockito.`when`(
            dbRepository.insertAll(
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyList(),
            )
        ).then { throw t }
    }

    private suspend fun runPageLoadingTest(
        loadType: LoadType,
        expected: RemoteMediator.MediatorResult
    ) {
        val actual = mediator.load(
            loadType,
            state,
        )

        when (actual) {
            is RemoteMediator.MediatorResult.Error -> {
                val expectedThrowable = (expected as RemoteMediator.MediatorResult.Error).throwable
                val actualThrowable  = actual.throwable
                assertEquals(expectedThrowable.toString(), actualThrowable.toString())
            }
            is RemoteMediator.MediatorResult.Success -> {
                val expectedEOP = (expected as RemoteMediator.MediatorResult.Success).endOfPaginationReached
                val actualEOP = actual.endOfPaginationReached
                assertEquals(expectedEOP, actualEOP)
            }
            else -> {
                assertEquals(expected.toString(), actual.toString())
            }
        }
    }

    private fun thenShouldHaveFetchedPage(page: Int) {
        Mockito
            .verify(api, Mockito.times(1))
            .getTopHeadlines(
                pageSize = GetTopHeadlinesFakeResponses.ARTICLES.size,
                page = page
            )
    }

    private fun thenShouldHaveInsertedAll(invalidate: Boolean = false, articles: List<Article>) {
        Mockito
            .verify(dbRepository, Mockito.times(1))
            .insertAll(
                invalidate = invalidate,
                articles = articles
            )
    }

    @Test
    fun `Load first page`(): Unit = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE)
        runPageLoadingTest(
            loadType = LoadType.REFRESH,
            expected = RemoteMediator.MediatorResult.Success(
                endOfPaginationReached = false
            )
        )
        thenShouldHaveFetchedPage(1)
        thenShouldHaveInsertedAll(
            invalidate = true,
            articles = GetTopHeadlinesFakeResponses.ARTICLES
        )
    }

    @Test
    fun `Should handle LoadType_PREPEND scenario`() = runBlocking {
        runPageLoadingTest(
            loadType = LoadType.PREPEND,
            expected = RemoteMediator.MediatorResult.Success(
                endOfPaginationReached = true
            )
        )
    }

    @Test
    fun `Should handle NewsApiStatus ERROR response`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.API_ERROR_RESPONSE)
        runPageLoadingTest(
            loadType = LoadType.REFRESH,
            expected = RemoteMediator.MediatorResult.Error(
                Throwable(InMemoryTopHeadlinesPagingSource.API_ERROR)
            )
        )
    }

    @Test
    fun `Should handle ThrowableNetworkResponse`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.THROWABLE_NETWORK_RESPONSE)
        runPageLoadingTest(
            loadType = LoadType.REFRESH,
            expected = RemoteMediator.MediatorResult.Error(
                GetTopHeadlinesFakeResponses.EXCEPTION
            )
        )
    }

    @Test
    fun `Should handle any other NetworkResponse`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.EMPTY_NETWORK_RESPONSE)
        runPageLoadingTest(
            loadType = LoadType.REFRESH,
            expected = RemoteMediator.MediatorResult.Error(
                Throwable(InMemoryTopHeadlinesPagingSource.UNKNOWN_NETWORK_ERROR)
            )
        )
    }

    @Test
    fun `Should handle API Exception`() = runBlocking {
        givenApiException(GetTopHeadlinesFakeResponses.EXCEPTION)
        runPageLoadingTest(
            loadType = LoadType.REFRESH,
            expected = RemoteMediator.MediatorResult.Error(
                GetTopHeadlinesFakeResponses.EXCEPTION
            )
        )
    }

    @Test
    fun `Should handle Database Exception`() = runBlocking {
        givenApiResponse(GetTopHeadlinesFakeResponses.SUCCESS_RESPONSE)
        givenDbException(GetTopHeadlinesFakeResponses.EXCEPTION)
        runPageLoadingTest(
            loadType = LoadType.REFRESH,
            expected = RemoteMediator.MediatorResult.Error(
                GetTopHeadlinesFakeResponses.EXCEPTION
            )
        )
    }
}