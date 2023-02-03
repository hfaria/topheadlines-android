package com.hfaria.ctw.topheadlines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.hfaria.ctw.topheadlines.databinding.FragmentTopHeadlinesBinding
import com.hfaria.ctw.topheadlines.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TopHeadlinesFragment : BaseFragment<TopHeadlinesViewModel>() {

    private lateinit var binding: FragmentTopHeadlinesBinding
    private lateinit var adapter: TopHeadlinesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopHeadlinesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.state = viewModel.state as TopHeadlinesScreenStateImpl
        adapter = TopHeadlinesAdapter(viewModel::onArticleClick)
        binding.rvTopHeadlines.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = viewModel.state as TopHeadlinesScreenStateImpl

        state.articleProfileRoute.observe(viewLifecycleOwner) { article->
            Toast
                .makeText(requireActivity(), "TITLE: ${article.title}", Toast.LENGTH_LONG)
                .show()
        }

        state.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast
                .makeText(requireActivity(), message, Toast.LENGTH_LONG)
                .show()
        }

        state.articlePage.observe(viewLifecycleOwner) { page ->
            lifecycleScope.launch {
                adapter.submitData(page)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { state ->
                if (state.refresh is LoadState.Loading) {
                    binding.cpiFetching.visibility = View.VISIBLE
                } else if (state.append.endOfPaginationReached) {
                    Toast
                        .makeText(requireActivity(), "Done loading completed challenges", Toast.LENGTH_LONG)
                        .show()
                } else {
                    binding.cpiFetching.visibility = View.GONE
                }
            }
        }

        viewModel.getTopHeadlines()
    }
}