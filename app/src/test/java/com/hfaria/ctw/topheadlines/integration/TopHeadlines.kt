package com.hfaria.ctw.topheadlines.integration

import com.hfaria.ctw.topheadlines.data.network.ErrorNetworkResponse
import com.hfaria.ctw.topheadlines.data.network.NewsApiResponse
import com.hfaria.ctw.topheadlines.data.network.SuccessNetworkResponse
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.integration.setup.NetworkTestComponent
import com.hfaria.ctw.topheadlines.integration.setup.BaseNetworkTest
import org.junit.Test
import javax.inject.Inject

class TopHeadlines: BaseNetworkTest() {

    override fun injectTest(component: NetworkTestComponent)
        = component.inject(this)

    @Inject
    lateinit var api: TopHeadlinesApi

    @Test
    fun test_get_top_headlines() {
        val response = api.getTopHeadlines() as SuccessNetworkResponse<NewsApiResponse>
        print(response.data)
    }
}