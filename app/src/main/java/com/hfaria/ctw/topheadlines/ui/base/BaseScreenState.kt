package com.hfaria.ctw.topheadlines.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface ErrorScreenState {
    val errorMessage: LiveData<String>
    val errorId: LiveData<Int>

    fun showErrorMessage(message: String)
    fun showErrorMessage(errorId: Int)
    fun showErrorMessage(throwable: Throwable)
}

interface LoadingScreenState {
    val isLoading: LiveData<Boolean>

    fun setLoadingState(state: Boolean)
}

abstract class BaseScreenState : ErrorScreenState, LoadingScreenState {

    override val errorMessage: LiveData<String>
        get() = _errorMessage
    protected val _errorMessage = MutableLiveData<String>()

    override val errorId: LiveData<Int>
        get() = _errorId
    protected val _errorId = MutableLiveData<Int>()

    override val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val _isLoading = MutableLiveData<Boolean>()

    override fun showErrorMessage(message: String) {
        _errorMessage.value = message
    }

    override fun showErrorMessage(errorId: Int) {
        _errorId.value = errorId
    }

    override fun showErrorMessage(throwable: Throwable) {
        showErrorMessage(throwable.toString())
    }

    override fun setLoadingState(state: Boolean) {
        _isLoading.value = state
    }
}