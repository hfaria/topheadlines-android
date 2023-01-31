package com.hfaria.ctw.topheadlines.data.network

/*
    Data class used to encapsulate any response
    to a data request.

    Could be used by any kind of data source
    to represent the result of a given data request.
 */
sealed class NetworkResponse<T>

data class SuccessNetworkResponse<T>(val data: T) : NetworkResponse<T>()
class EmptyNetworkResponse<T>: NetworkResponse<T>()
class NotFoundNetworkResponse<T>: NetworkResponse<T>()
data class ErrorNetworkResponse<T>(val error: String) : NetworkResponse<T>()
data class ThrowableNetworkResponse<T>(val data: Throwable) : NetworkResponse<T>()
