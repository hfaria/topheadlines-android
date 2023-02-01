package com.hfaria.ctw.topheadlines.ui

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.cleanarch.codewars.demo.ui.base.BaseScreenState
import com.hfaria.ctw.topheadlines.data.repository.TopHeadlinesRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopHeadlinesScreenState: BaseScreenState()

class TopHeadlinesViewModel @Inject constructor(
    private val topHeadlines: TopHeadlinesRepository
) : ViewModel() {

    val state = TopHeadlinesScreenState()

    fun getTopHeadlines() {
        topHeadlines.get()
            .cachedIn(viewModelScope)
            .map {
                state.showErrorMessage("OK")
            }
    }
}
