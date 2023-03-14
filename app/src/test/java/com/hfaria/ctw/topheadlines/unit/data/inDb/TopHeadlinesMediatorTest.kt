package com.hfaria.ctw.topheadlines.unit.data.inDb

import androidx.paging.*
import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import com.hfaria.ctw.topheadlines.data.network.NetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
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
class InMemoryTopHeadlinesPagingSourceLoadTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var api: TopHeadlinesApi

    @Mock
    lateinit var state: PagingState<Int, Article>

    lateinit var mediator: TopHeadlinesMediator

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

    @Before
    fun setup() {
        mediator = TopHeadlinesMediator(
            mainDispatcherRule.testDispatcher,
            api,
            GetTopHeadlinesFakeResponses.ARTICLES.size
        )
    }

    private suspend fun runPageLoadingTest(
        loadType: LoadType,
        expected: RemoteMediator.MediatorResult
    ) {
        val actual = mediator.load(
            loadType,
            state,
        )

        if (actual is RemoteMediator.MediatorResult.Error) {
            val expectedThrowable = (expected as RemoteMediator.MediatorResult.Error).throwable
            val actualThrowable  = actual.throwable
            assertEquals(expectedThrowable, actualThrowable)
        } else {
            assertEquals(expected.toString(), actual.toString())
        }
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
}