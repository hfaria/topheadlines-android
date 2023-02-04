package com.hfaria.ctw.topheadlines.unit.data

import androidx.paging.PagingSource
import com.hfaria.ctw.topheadlines.data.network.GetTopHeadlinesResponse
import com.hfaria.ctw.topheadlines.data.network.SuccessNetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.domain.Article
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

val ARTICLES = listOf(Article("A1"), Article("A2"))
val SUCCESS_RESPONSE = SuccessNetworkResponse(
    GetTopHeadlinesResponse(
        totalResults = ARTICLES.size,
        articles = ARTICLES
    )
)

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryTopHeadlinesPagingSourceTest {

    @Mock
    lateinit var api: TopHeadlinesApi

    lateinit var source: InMemoryTopHeadlinesPagingSource

    @Before
    fun setup() {
        source = InMemoryTopHeadlinesPagingSource(api, 2)
    }

    @Test
    fun `Load first page`() = runBlocking {
        `when`(api.getTopHeadlines(
            anyString(),
            anyInt(),
            anyInt()
        )).thenReturn(SUCCESS_RESPONSE)

        val expected = PagingSource.LoadResult.Page(
            data = ARTICLES,
            prevKey = null,
            nextKey = 2
        )
        val actual = withContext(this.coroutineContext) {
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }
        assertEquals(expected.toString(), actual.toString())
    }
}