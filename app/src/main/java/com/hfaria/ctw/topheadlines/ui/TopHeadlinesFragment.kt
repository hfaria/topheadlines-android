package com.hfaria.ctw.topheadlines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.hfaria.ctw.topheadlines.databinding.FragmentTopHeadlinesBinding
import com.hfaria.ctw.topheadlines.ui.article_content.ArticleContentActivity
import com.hfaria.ctw.topheadlines.ui.base.BaseFragment
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
        binding.state = viewModel.state
        adapter = TopHeadlinesAdapter(viewModel::onArticleClick)
        binding.rvTopHeadlines.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservables(viewModel.state)
        setupRoutes(viewModel.state)
        viewModel.getTopHeadlines()
    }

    private fun setupObservables(state: TopHeadlinesScreenState) {
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
    }

    private fun setupRoutes(state: TopHeadlinesScreenState) {
        state.articleProfileRoute.observe(viewLifecycleOwner) { article->
            ArticleContentActivity.open(requireContext(), article)
        }
    }
}


