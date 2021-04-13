package com.example.administrator.firstapplication.Bean

import android.os.Parcel
import android.os.Parcelable

data  class Url(val id:Int,
                val url:String,
                val childname:String,
                val length: Int):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(url)
        parcel.writeString(childname)
        parcel.writeInt(length)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Url> {
        override fun createFromParcel(parcel: Parcel): Url {
            return Url(parcel)
        }

        override fun newArray(size: Int): Array<Url?> {
            return arrayOfNulls(size)
        }
    }
}