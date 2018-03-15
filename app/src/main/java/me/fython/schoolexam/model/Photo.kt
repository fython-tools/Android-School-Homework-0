package me.fython.schoolexam.model

import android.os.Parcel
import android.os.Parcelable

data class Photo(
        val id: String,
        val width: Int,
        val height: Int,
        val color: String,
        val likes: Int,
        val description: String?,
        val urls: UrlList
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readParcelable(UrlList::class.java.classLoader))

    data class UrlList(
            val raw: String,
            val full: String,
            val regular: String,
            val small: String,
            val thumb: String
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(raw)
            parcel.writeString(full)
            parcel.writeString(regular)
            parcel.writeString(small)
            parcel.writeString(thumb)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<UrlList> {
            override fun createFromParcel(parcel: Parcel): UrlList {
                return UrlList(parcel)
            }

            override fun newArray(size: Int): Array<UrlList?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(color)
        parcel.writeInt(likes)
        parcel.writeString(description)
        parcel.writeParcelable(urls, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Photo> {
        override fun createFromParcel(parcel: Parcel): Photo {
            return Photo(parcel)
        }

        override fun newArray(size: Int): Array<Photo?> {
            return arrayOfNulls(size)
        }
    }

}