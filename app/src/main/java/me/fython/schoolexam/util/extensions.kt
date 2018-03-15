package me.fython.schoolexam.util

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

inline fun <reified T> classOf() = T::class.java

fun ViewGroup.inflate(@LayoutRes layoutResId: Int): View =
        LayoutInflater.from(this.context).inflate(layoutResId, this, false)

@JvmName("setOnLoadMoreListener")
fun RecyclerView.onLoadMore(action: () -> Unit) {
    this.addOnScrollListener(OnRVLoadMoreListener(object : OnListLoadMoreListener {
        override fun onLoadMore() {
            action.invoke()
        }
    }))
}