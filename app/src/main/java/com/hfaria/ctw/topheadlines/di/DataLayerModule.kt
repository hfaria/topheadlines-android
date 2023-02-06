package com.hfaria.ctw.topheadlines.di

import com.hfaria.ctw.topheadlines.BuildConfig
import com.hfaria.ctw.topheadlines.data.network.NetworkResponseCallAdapterFactory
import com.hfaria.ctw.topheadlines.data.network.TopHeadlinesApi
import com.hfaria.ctw.topheadlines.data.repository.InMemoryTopHeadlinesRepository
import com.hfaria.ctw.topheadlines.data.repository.TopHeadlinesRepository
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class DataLayerModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(
                Interceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.header("X-Api-Key", BuildConfig.API_KEY)
                    return@Interceptor chain.proceed(builder.build())
                }
            )
        }.build()
    }

    @Singleton
    @Provides
    fun provideTopHeadlinesApi(okHttpClient: OkHttpClient): TopHeadlinesApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NEWS_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(TopHeadlinesApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTopHeadlinesRepository(api: TopHeadlinesApi): TopHeadlinesRepository {
        return InMemoryTopHeadlinesRepository(api, BuildConfig.TOP_HEADLINES_PAGE_SIZE)
    }
}
