package com.hfaria.ctw.topheadlines.di

import androidx.lifecycle.ViewModel
import com.hfaria.ctw.topheadlines.ui.top_headlines.TopHeadlinesFragment
import com.hfaria.ctw.topheadlines.ui.top_headlines.TopHeadlinesScreenState
import com.hfaria.ctw.topheadlines.ui.top_headlines.TopHeadlinesScreenStateImpl
import com.hfaria.ctw.topheadlines.ui.top_headlines.TopHeadlinesViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class TopHeadlinesModule {

    @ContributesAndroidInjector
    abstract fun contributesTopHeadlinesFragment(): TopHeadlinesFragment

    @Binds
    @IntoMap
    @ViewModelKey(TopHeadlinesViewModel::class)
    abstract fun bindViewModel(viewmodel: TopHeadlinesViewModel): ViewModel

    companion object {
        @Provides
        fun providesTopHeadlinesScreenState(): TopHeadlinesScreenState {
            return TopHeadlinesScreenStateImpl()
        }
    }
}
