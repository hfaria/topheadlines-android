package com.hfaria.ctw.topheadlines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hfaria.ctw.topheadlines.databinding.FragmentTopHeadlinesBinding
import com.hfaria.ctw.topheadlines.ui.base.BaseFragment

class TopHeadlinesFragment : BaseFragment<TopHeadlinesViewModel>() {

    private lateinit var binding: FragmentTopHeadlinesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopHeadlinesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.state = viewModel.state
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.routes.userProfileRoute.observe(viewLifecycleOwner) { user ->
        //    Toast
        //        .makeText(requireActivity(), "NAME: ${user.name.orEmpty()}", Toast.LENGTH_LONG)
        //        .show()
        //}

        viewModel.state.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast
                .makeText(requireActivity(), message, Toast.LENGTH_LONG)
                .show()
        }

        viewModel.getTopHeadlines()
    }
}