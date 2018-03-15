package me.fython.schoolexam.util

import android.content.Context
import android.net.ConnectivityManager

import java.io.IOException

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by rikka on 2017/11/24.
 */

class HttpCacheInterceptor(context: Context) : Interceptor {

    private val mConnectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()

        val response = chain.proceed(request)

        if (mConnectivityManager.isDefaultNetworkActive) {
            val maxAge = 60 * 60
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .build()
        } else {
            val maxStale = Integer.MAX_VALUE
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
        }
        return response
    }
}
