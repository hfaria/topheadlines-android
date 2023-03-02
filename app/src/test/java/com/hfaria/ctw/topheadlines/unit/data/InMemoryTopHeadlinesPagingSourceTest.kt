package com.hfaria.ctw.topheadlines.unit.data

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesPagingSource
import com.hfaria.ctw.topheadlines.domain.Article
import com.hfaria.ctw.topheadlines.unit.mock.GetTopHeadlinesFakeResponses.ARTICLES
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryTopHeadlinesPagingSourceGetRefreshKeyTest {

    @Mock
    lateinit var api: TopHeadlinesApi

    lateinit var source: InMemoryTopHeadlinesPagingSource

    @Before
    fun setup() {
        source = InMemoryTopHeadlinesPagingSource(
            TestCoroutineDispatcher(),
            api,
            ARTICLES.size
        )
    }

    @Test
    fun `getRefreshKey should return the next key when prevKey is null`() {
        // Given
        val state = createPagingState(prevKey = null, nextKey = 3)

        // When
        val refreshKey = source.getRefreshKey(state)

        // Then
        assertEquals(2, refreshKey)
    }

    @Test
    fun `getRefreshKey should return the prev key when nextKey is null`() {
        // Given
        val state = createPagingState(prevKey = 1, nextKey = null)

        // When
        val refreshKey = source.getRefreshKey(state)

        // Then
        assertEquals(2, refreshKey)
    }

    @Test
    fun `getRefreshKey should return null when both prevKey and nextKey are null`() {
        // Given
        val state = createPagingState(prevKey = null, nextKey = null)

        // When
        val refreshKey = source.getRefreshKey(state)

        // Then
        assertEquals(null, refreshKey)
    }

    private fun createPagingState(prevKey: Int?, nextKey: Int?): PagingState<Int, Article> {
        val closestPage = createClosestPage(prevKey, nextKey)
        return PagingState(
            pages = listOf(closestPage),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 1),
            leadingPlaceholderCount = 0
        )
    }

    private fun createClosestPage(prevKey: Int?, nextKey: Int?): PagingSource.LoadResult.Page<Int, Article> {
        return PagingSource.LoadResult.Page(
            data = listOf(Article(title = "FOOBAR")),
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}