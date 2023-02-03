package com.hfaria.ctw.topheadlines.ui.article_content

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.add
import com.hfaria.ctw.topheadlines.R
import com.hfaria.ctw.topheadlines.domain.Article

class ArticleContentActivity : AppCompatActivity(R.layout.activity_article_content) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<ArticleContentFragment>(R.id.fragment_container_view)
            }
        }
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