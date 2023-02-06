package com.hfaria.ctw.topheadlines.ui.top_headlines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfaria.ctw.topheadlines.R
import com.hfaria.ctw.topheadlines.domain.Article
import com.squareup.picasso.Picasso

class TopHeadlinesAdapter(
    private val onClick: (Article) -> Unit
) : PagingDataAdapter<Article, TopHeadlinesAdapter.ViewHolder>(TopHeadlinesDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.top_headlines_list_item, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    class ViewHolder(
        itemView: View,
        val onClick: (Article) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val ivArticleImage: AppCompatImageView = itemView.findViewById(R.id.iv_article_image)
        private var article: Article? = null

        init {
            itemView.setOnClickListener {
                article?.let {
                    onClick(it)
                }
            }
        }

        fun bind(article: Article?) {
            this.article = article
            tvTitle.text = article?.title
            article?.urlToImage?.let {
                Picasso.get().load(it)
                    .fit()
                    .centerCrop()
                    .into(ivArticleImage)
            }
        }
    }
}

object TopHeadlinesDiffCallback: DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}