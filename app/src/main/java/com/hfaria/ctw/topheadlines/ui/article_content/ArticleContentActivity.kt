package com.hfaria.ctw.topheadlines.ui.article_content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.hfaria.ctw.topheadlines.BuildConfig
import com.hfaria.ctw.topheadlines.R
import com.hfaria.ctw.topheadlines.domain.Article

class ArticleContentActivity : AppCompatActivity(R.layout.activity_article_content) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(BuildConfig.NEWS_SOURCE_TITLE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<ArticleContentFragment>(R.id.fragment_container_view)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val KEY_ARTICLE = "article"

        fun open(context: Context, article: Article) {
            val intent = Intent(context, ArticleContentActivity::class.java)
            intent.putExtra(KEY_ARTICLE, article)
            context.startActivity(intent)
        }
    }
}