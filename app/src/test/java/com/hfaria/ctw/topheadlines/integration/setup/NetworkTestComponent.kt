package com.hfaria.ctw.topheadlines.integration.setup

import com.hfaria.ctw.topheadlines.integration.GetTopHeadlinesTest
import com.hfaria.ctw.topheadlines.di.AppComponent
import com.hfaria.ctw.topheadlines.di.DataLayerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        DataLayerModule::class,
    ]
)
interface NetworkTestComponent : AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(baseTest: BaseNetworkTest): Builder

        fun build(): NetworkTestComponent
    }

    fun inject(test: GetTopHeadlinesTest)
}