package com.hfaria.ctw.topheadlines.ui.article_content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hfaria.ctw.topheadlines.databinding.FragmentArticleContentBinding
import com.hfaria.ctw.topheadlines.domain.Article
import com.squareup.picasso.Picasso

class ArticleContentFragment : Fragment() {

    private lateinit var binding: FragmentArticleContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val article = requireActivity().intent
            .getSerializableExtra(ArticleContentActivity.KEY_ARTICLE) as Article
        binding = FragmentArticleContentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.article = article
        article.urlToImage?.let {
            Picasso.get().load(it).into(binding.ivArticleBanner)
        }
        return binding.root
    }
}