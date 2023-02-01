package com.hfaria.ctw.topheadlines.ui

import androidx.lifecycle.*
import com.cleanarch.codewars.demo.ui.base.BaseScreenState
import javax.inject.Inject

class TopHeadlinesScreenState: BaseScreenState() {
}

class TopHeadlinesViewModel @Inject constructor(
) : ViewModel() {

    val state = TopHeadlinesScreenState()

    fun handleUserSearch() {
        //viewModelScope.launch {
        //    runner.run(useCase, state.username.value.orEmpty(), presenter)
        //}
    }
}
