package com.hfaria.ctw.topheadlines.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.add
import com.hfaria.ctw.topheadlines.R

class TopHeadlinesActivity : AppCompatActivity(R.layout.activity_top_headlines) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<TopHeadlinesFragment>(R.id.fragment_container_view)
            }
        }
    }
}