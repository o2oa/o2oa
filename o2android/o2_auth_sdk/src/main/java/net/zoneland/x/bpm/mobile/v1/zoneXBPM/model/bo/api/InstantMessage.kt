package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by fancyLou on 2020-05-25.
 * Copyright © 2020 O2. All rights reserved.
 */

data class InstantMessage(
        var id: String = "",
        var title: String = "",
        var type: String = "",
        var body: String = "",
        var person: String = "",
        var consumerList: ArrayList<String> = ArrayList(),
        var consumed: Boolean = false,
        var createTime: String = "",
        var updateTime: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.createStringArrayList() ?: ArrayList(),
            1 == source.readInt(),
            source.readString() ?: "",
            source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(title)
        writeString(type)
        writeString(body)
        writeString(person)
        writeStringList(consumerList)
        writeInt((if (consumed) 1 else 0))
        writeString(createTime)
        writeString(updateTime)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<InstantMessage> = object : Parcelable.Creator<InstantMessage> {
            override fun createFromParcel(source: Parcel): InstantMessage = InstantMessage(source)
            override fun newArray(size: Int): Array<InstantMessage?> = arrayOfNulls(size)
        }
    }
}