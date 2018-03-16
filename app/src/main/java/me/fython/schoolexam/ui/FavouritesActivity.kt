package me.fython.schoolexam.ui

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.fython.schoolexam.R
import me.fython.schoolexam.dao.FavouritesDatabase
import me.fython.schoolexam.util.OkHttpUtils

class FavouritesActivity : AppCompatActivity() {

    private val listView by lazy { findViewById<RecyclerView>(android.R.id.list) }
    private val refreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.refresh_layout) }
    private val adapter = ThumbsListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OkHttpUtils.init(this.applicationContext)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter

        refreshLayout.setColorSchemeResources(R.color.colorAccent)
        refreshLayout.setOnRefreshListener {
            refresh()
        }

        refresh()
    }

    override fun onPause() {
        super.onPause()
        FavouritesDatabase.getInstance(this).save()
    }

    private fun refresh() {
        async(UI) {
            refreshLayout.isRefreshing = true
            val list = async(CommonPool) {
                FavouritesDatabase.getInstance(this@FavouritesActivity)
                        .list()
            }.await()
            adapter.data.clear()
            adapter.data.addAll(list)
            adapter.notifyDataSetChanged()
            listView.scheduleLayoutAnimation()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}