package com.hfaria.ctw.topheadlines.unit.data

import com.hfaria.ctw.topheadlines.data.network.*
import com.hfaria.ctw.topheadlines.unit.data.RetrofitFakeResponses.EMPTY_RESPONSE
import com.hfaria.ctw.topheadlines.unit.data.RetrofitFakeResponses.EXCEPTION
import com.hfaria.ctw.topheadlines.unit.data.RetrofitFakeResponses.GENERIC_ERROR_RESPONSE
import com.hfaria.ctw.topheadlines.unit.data.RetrofitFakeResponses.NOT_FOUND_RESPONSE
import com.hfaria.ctw.topheadlines.unit.data.RetrofitFakeResponses.SUCCESS_RESPONSE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Response

object RetrofitFakeResponses {

    val SUCCESS_RESPONSE = Response.success(GetTopHeadlinesResponse())

    val EMPTY_RESPONSE = Response.success<GetTopHeadlinesResponse>(null)

    val NOT_FOUND_RESPONSE = Response.error<GetTopHeadlinesResponse>(
        404,
        ResponseBody.create(null, "")
    )

    val GENERIC_ERROR = "GENERIC_ERROR"
    val GENERIC_ERROR_RESPONSE = Response.error<GetTopHeadlinesResponse>(
        425,
        ResponseBody.create(null, GENERIC_ERROR )
    )

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
    fun `Should handle success response`() = runBlocking {
        `when`(call.execute()).thenReturn(SUCCESS_RESPONSE)

        val expected = SuccessNetworkResponse(
            GetTopHeadlinesResponse()
        )

        val actual = adapter.adapt(call)
        assertEquals(expected.toString(), actual.toString())
    }

    @Test
    fun `Should handle empty response`() = runBlocking {
        `when`(call.execute()).thenReturn(EMPTY_RESPONSE)
        val networkResponse = adapter.adapt(call)
        assertTrue(networkResponse is EmptyNetworkResponse)
    }

    @Test
    fun `Should handle 404 - Not Found response`() = runBlocking {
        `when`(call.execute()).thenReturn(NOT_FOUND_RESPONSE)
        val networkResponse = adapter.adapt(call)
        assertTrue(networkResponse is NotFoundNetworkResponse)
    }

    @Test
    fun `Should handle any HTTP error response`() = runBlocking {
        `when`(call.execute()).thenReturn(GENERIC_ERROR_RESPONSE)
        val networkResponse = adapter.adapt(call)
        assertTrue(networkResponse is ErrorNetworkResponse)
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