package com.hfaria.ctw.topheadlines.data.network

import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseCallAdapterFactory : Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != NetworkResponse::class.java) {
            return null
        }
        val bodyType = getParameterUpperBound(0, returnType as ParameterizedType)
        return NetworkResponseCallAdapter<Any>(bodyType)
    }
}
