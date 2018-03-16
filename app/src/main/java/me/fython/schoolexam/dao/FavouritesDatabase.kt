package me.fython.schoolexam.dao

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.fython.schoolexam.model.Photo

class FavouritesDatabase(context: Context)
    : SimpleDatabase<MutableList<Photo>>(context, "favourites") {

    override fun getDefaultData(): MutableList<Photo> = mutableListOf()

    override fun parseJsonToData(json: String): MutableList<Photo>? {
        return Gson().fromJson<Array<Photo>>(json, object : TypeToken<Array<Photo>>(){}.type)
                .toMutableList()
    }

    operator fun plusAssign(photo: Photo) {
        data.add(photo)
    }

    operator fun minusAssign(photo: Photo) {
        data.removeAll { it.id == photo.id }
    }

    operator fun minusAssign(id: String) {
        data.removeAll { it.id == id }
    }

    operator fun contains(photo: Photo?): Boolean {
        return data.contains(photo)
    }

    operator fun contains(id: String?): Boolean {
        return data.find { it.id == id } != null
    }

    fun list(): MutableList<Photo> {
        return data
    }

    companion object {

        private var sInstance: FavouritesDatabase? = null

        fun getInstance(context: Context): FavouritesDatabase {
            if (sInstance == null) {
                sInstance = FavouritesDatabase(context)
            }
            return sInstance ?: getInstance(context)
        }

    }

}