package com.hfaria.ctw.topheadlines.unit.data

import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.unit.data.RetrofitFakeResponses.EXCEPTION
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call

object RetrofitFakeResponses {

    val EXCEPTION = Exception("RetrofitFakeException")
}

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class NetworkResponseCallAdapterTest {

    @Mock
    lateinit var call: Call<GetTopHeadlinesResponse>

    lateinit var adapter: NetworkResponseCallAdapter<GetTopHeadlinesResponse>

    @Before
    fun setup() {
        adapter = NetworkResponseCallAdapter(GetTopHeadlinesResponse::class.java)
    }

    @Test
    fun `Should handle Network Exception`() = runBlocking {
        `when`(call.execute()).then { throw EXCEPTION }

        val expected = ThrowableNetworkResponse<GetTopHeadlinesResponse>(
            EXCEPTION
        )

        val actual = adapter.adapt(call)
        assertEquals(expected.toString(), actual.toString())
    }
}