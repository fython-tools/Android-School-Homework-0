package me.fython.schoolexam.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

object OkHttpUtils {

    lateinit var client : OkHttpClient

    fun init(context: Context) {
        client = OkHttpClient.Builder()
                .cache(Cache(File(context.cacheDir, "api"), (1024 * 1024 * 100).toLong()))
                .addNetworkInterceptor(HttpCacheInterceptor(context))
                .build()
    }

    fun newCall(request: Request): Call = client.newCall(request)

    fun <T> requestJsonObject(url: String, clazz: Class<T>): T {
        return newCall(Request.Builder().url(url).build()).execute().let {
            if (it.isSuccessful && it.code() == 200) {
                Gson().fromJson(it.body()!!.string(), clazz)
            } else {
                throw IOException()
            }
        }
    }

    fun <T> requestJsonObject(url: String, type: Type): T {
        return newCall(Request.Builder().url(url).build()).execute().let {
            if (it.isSuccessful && it.code() == 200) {
                Gson().fromJson(it.body()!!.string(), type)
            } else {
                throw IOException()
            }
        }
    }

    inline fun <reified T> requestJsonObject(url: String): T {
        return requestJsonObject(url, object : TypeToken<T>() {}.type)
    }

}