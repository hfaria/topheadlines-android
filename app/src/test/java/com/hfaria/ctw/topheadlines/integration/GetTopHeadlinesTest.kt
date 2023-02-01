package com.hfaria.ctw.topheadlines.integration

import com.hfaria.ctw.topheadlines.data.network.NewsApiResponse
import com.hfaria.ctw.topheadlines.data.network.NewsApiStatus
import com.hfaria.ctw.topheadlines.data.network.SuccessNetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.integration.setup.NetworkTestComponent
import com.hfaria.ctw.topheadlines.integration.setup.BaseNetworkTest
import junit.framework.Assert.assertEquals
import org.junit.Test
import javax.inject.Inject

class GetTopHeadlinesTest: BaseNetworkTest() {

    override fun injectTest(component: NetworkTestComponent)
        = component.inject(this)

    @Inject
    lateinit var api: TopHeadlinesApi

    @Test
    fun test_get_top_headlines() {
        val response = api.getTopHeadlines() as SuccessNetworkResponse<NewsApiResponse>
        assertEquals(NewsApiStatus.OK, response.data.status)
        print(response.data)
    }
}