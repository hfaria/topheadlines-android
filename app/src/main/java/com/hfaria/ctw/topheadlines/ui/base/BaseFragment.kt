package com.hfaria.ctw.topheadlines.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment<VM>: Fragment() {

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    protected val viewModel: VM by viewModels {
        viewModelFactory
    }

    override fun onAttach(ctx: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(ctx)
    }

}