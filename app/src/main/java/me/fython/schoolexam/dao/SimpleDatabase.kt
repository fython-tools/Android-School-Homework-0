package me.fython.schoolexam.dao

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class SimpleDatabase<E : Any>(context: Context, name: String? = null) {

    private val sharedPreferences = context.getSharedPreferences(
            name ?: javaClass.simpleName, Context.MODE_PRIVATE)

    protected open val keyName = "data"

    protected lateinit var data: E

    init {
        load()
    }

    abstract fun getDefaultData(): E

    protected open fun parseJsonToData(json: String): E? {
        return Gson().fromJson(json, object : TypeToken<E>(){}.type)
    }

    fun load() {
        val value = sharedPreferences.getString(keyName, null)
        if (value != null) {
            data = parseJsonToData(value) ?: getDefaultData()
        } else {
            data = getDefaultData()
        }
    }

    fun save() {
        sharedPreferences.edit().putString(keyName, Gson().toJson(data)).apply()
    }

}