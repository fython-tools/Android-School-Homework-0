package me.fython.schoolexam.api

import android.net.Uri
import me.fython.schoolexam.model.Photo
import me.fython.schoolexam.util.OkHttpUtils.requestJsonObject

object UnsplashApi {

    const val ACCESS_KEY = UNSPLASH_ACCESS_KEY

    const val ORDER_BY_LATEST = "latest"
    const val ORDER_BY_OLDEST = "oldest"
    const val ORDER_BY_POPULAR = "popular"

    private val API_HOST_URI = Uri.Builder().scheme("https")
            .authority("api.unsplash.com")
            .appendQueryParameter("client_id", ACCESS_KEY)
            .build()

    fun getListUri(page: Int, perPage: Int, orderBy: String): Uri {
        return API_HOST_URI.buildUpon()
                .path("photos")
                .appendQueryParameter("page", page.toString())
                .appendQueryParameter("per_page", perPage.toString())
                .appendQueryParameter("order_by", orderBy)
                .build()
    }

    fun getList(page: Int, perPage: Int = 10, orderBy: String = ORDER_BY_LATEST): List<Photo> {
        return requestJsonObject<Array<Photo>>(getListUri(page, perPage, orderBy).toString()).toList()
    }

}