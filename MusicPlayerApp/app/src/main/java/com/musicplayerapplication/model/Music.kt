package com.musicplayerapplication.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Music(val name: String?, val albumArtPathId: String?): Parcelable {

    constructor(source: Parcel): this(source.readString(), source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.name)
        dest?.writeString(this.albumArtPathId)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<Music> = object : Parcelable.Creator<Music> {
            override fun createFromParcel(source: Parcel): Music{
                return Music(source)
            }

            override fun newArray(size: Int): Array<Music?> {
                return arrayOfNulls(size)
            }
        }
    }
}