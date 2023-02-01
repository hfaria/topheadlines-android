package com.hfaria.ctw.topheadlines.integration

import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.integration.setup.NetworkTestComponent
import com.hfaria.ctw.topheadlines.integration.setup.BaseNetworkTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.inject.Inject

class GetTopHeadlinesTest: BaseNetworkTest() {

    override fun injectTest(component: NetworkTestComponent)
        = component.inject(this)

    @Inject
    lateinit var api: TopHeadlinesApi

    @Test
    fun test_get_top_headlines_page() {
        val response = api.getTopHeadlines() as SuccessNetworkResponse<GetTopHeadlinesResponse>
        checkData(response.data)
    }

    @Test
    fun test_get_all_top_headlines() {
        val pageSize = 20
        var page = 1

        do {
            val response = api.getTopHeadlines(
                pageSize = pageSize,
                page = page
            ) as SuccessNetworkResponse<GetTopHeadlinesResponse>
            val data = response.data
            debugPrint(page, data)
            checkData(data)
            page++
        } while (data.articles.size >= pageSize)
    }

    private fun checkData(data: GetTopHeadlinesResponse) {
        assertEquals(NewsApiStatus.OK, data.status)
        assertTrue(data.totalResults > 0)
        assertTrue(data.articles.isNotEmpty())
    }

    private fun debugPrint(page: Int, data: GetTopHeadlinesResponse) {
        println("[PAGE=$page, TOTAL_RESULTS=${data.totalResults}, ARTICLES=${data.articles.size}]")
        data.articles.forEach {
            println("TITLE: ${it.title.orEmpty()}")
        }
        println("-----------------------------------------")
    }
}