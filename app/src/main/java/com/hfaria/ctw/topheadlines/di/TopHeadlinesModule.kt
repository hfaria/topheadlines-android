package com.hfaria.ctw.topheadlines.di

import androidx.lifecycle.ViewModel
import com.hfaria.ctw.topheadlines.ui.TopHeadlinesFragment
import com.hfaria.ctw.topheadlines.ui.TopHeadlinesViewModel
import dagger.Binds
import dagger.Module
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

}
