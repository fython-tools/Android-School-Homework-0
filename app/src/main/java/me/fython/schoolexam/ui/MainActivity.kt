package me.fython.schoolexam.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.fython.schoolexam.R
import me.fython.schoolexam.api.UnsplashApi
import me.fython.schoolexam.dao.FavouritesDatabase
import me.fython.schoolexam.util.OkHttpUtils
import me.fython.schoolexam.util.onLoadMore

class MainActivity : AppCompatActivity() {

    private val listView by lazy { findViewById<RecyclerView>(android.R.id.list) }
    private val refreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.refresh_layout) }
    private val adapter = ThumbsListAdapter()

    private var currentPage = 1

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OkHttpUtils.init(this.applicationContext)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter
        listView.onLoadMore {
            if (!refreshLayout.isRefreshing) {
                currentPage++
                refresh(shouldClear = false)
            }
        }

        refreshLayout.setColorSchemeResources(R.color.colorAccent)
        refreshLayout.setOnRefreshListener {
            refresh(shouldClear = true)
        }

        if (savedInstanceState == null) {
            refresh()
        } else {
            currentPage = savedInstanceState[STATE_CURRENT_PAGE] as Int
            adapter.data.clear()
            adapter.data.addAll(savedInstanceState.getParcelableArrayList(STATE_LIST))
            adapter.notifyDataSetChanged()
            if (adapter.data.isEmpty()) {
                refresh()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_PAGE, currentPage)
        outState.putParcelableArrayList(STATE_LIST, ArrayList(adapter.data))
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        FavouritesDatabase.getInstance(this).save()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelRefresh()
    }

    private fun refresh(shouldClear: Boolean = true) {
        job = async(UI) {
            refreshLayout.isRefreshing = true
            try {
                val result = async(CommonPool) {
                    UnsplashApi.getList(currentPage)
                }.await()
                if (shouldClear) adapter.data.clear()
                adapter.data += result
                adapter.notifyDataSetChanged()
                if (shouldClear) listView.scheduleLayoutAnimation()
            } catch (e : Exception) {
                e.printStackTrace()
            }
            refreshLayout.isRefreshing = false
        }
    }

    private fun cancelRefresh() {
        job?.cancel()
        job = null
        refreshLayout.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_favourites -> {
            startActivity(Intent(this, FavouritesActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        }
        R.id.action_refresh -> {
            refresh()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {

        private const val STATE_CURRENT_PAGE = "current_page"
        private const val STATE_LIST = "list"

    }

}
