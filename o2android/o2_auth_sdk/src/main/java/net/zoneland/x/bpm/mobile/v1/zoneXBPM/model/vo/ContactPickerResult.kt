package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fancyLou on 2019-08-27.
 * Copyright © 2019 O2. All rights reserved.
 */

class ContactPickerResult(
        var departments: ArrayList<O2UnitPickerResultItem>,
        var identities: ArrayList<O2IdentityPickerResultItem>,
        var groups: ArrayList<O2GroupPickerResultItem>,
        var users: ArrayList<O2PersonPickerResultItem>
) : Parcelable {
    constructor(source: Parcel) : this(
            source.createTypedArrayList(O2UnitPickerResultItem.CREATOR) ?: ArrayList(),
            source.createTypedArrayList(O2IdentityPickerResultItem.CREATOR) ?: ArrayList(),
            source.createTypedArrayList(O2GroupPickerResultItem.CREATOR) ?: ArrayList(),
            source.createTypedArrayList(O2PersonPickerResultItem.CREATOR) ?: ArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(departments)
        writeTypedList(identities)
        writeTypedList(groups)
        writeTypedList(users)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ContactPickerResult> = object : Parcelable.Creator<ContactPickerResult> {
            override fun createFromParcel(source: Parcel): ContactPickerResult = ContactPickerResult(source)
            override fun newArray(size: Int): Array<ContactPickerResult?> = arrayOfNulls(size)
        }
    }
}


class O2UnitPickerResultItem(
        var id: String = "",
        var name: String = "",
        var unique: String = "",
        var distinguishedName: String = "",
        var typeList: ArrayList<String> = ArrayList(),
        var shortName: String = "",
        var level: Int = -1,
        var levelName: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.createStringArrayList() ?: ArrayList(),
            source.readString() ?: "",
            source.readInt(),
            source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(unique)
        writeString(distinguishedName)
        writeStringList(typeList)
        writeString(shortName)
        writeInt(level)
        writeString(levelName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<O2UnitPickerResultItem> = object : Parcelable.Creator<O2UnitPickerResultItem> {
            override fun createFromParcel(source: Parcel): O2UnitPickerResultItem = O2UnitPickerResultItem(source)
            override fun newArray(size: Int): Array<O2UnitPickerResultItem?> = arrayOfNulls(size)
        }
    }
}

class O2IdentityPickerResultItem(
        var id: String = "",
        var name: String = "",
        var unique: String = "",
        var distinguishedName: String = "",
        var person: String = "",
        var unit: String = "",
        var unitName: String = "",
        var personName: String = "",
        var personUnique: String = "",
        var personDn: String = "",
        var unitLevel: Int = -1,
        var unitLevelName: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readInt(),
            source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(unique)
        writeString(distinguishedName)
        writeString(person)
        writeString(unit)
        writeString(unitName)
        writeString(personName)
        writeString(personUnique)
        writeString(personDn)
        writeInt(unitLevel)
        writeString(unitLevelName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<O2IdentityPickerResultItem> = object : Parcelable.Creator<O2IdentityPickerResultItem> {
            override fun createFromParcel(source: Parcel): O2IdentityPickerResultItem = O2IdentityPickerResultItem(source)
            override fun newArray(size: Int): Array<O2IdentityPickerResultItem?> = arrayOfNulls(size)
        }
    }
}

class O2GroupPickerResultItem(
        var id: String = "",
        var name: String = "",
        var unique: String = "",
        var distinguishedName: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(unique)
        writeString(distinguishedName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<O2GroupPickerResultItem> = object : Parcelable.Creator<O2GroupPickerResultItem> {
            override fun createFromParcel(source: Parcel): O2GroupPickerResultItem = O2GroupPickerResultItem(source)
            override fun newArray(size: Int): Array<O2GroupPickerResultItem?> = arrayOfNulls(size)
        }
    }
}

class O2PersonPickerResultItem(
        var id: String = "",
        var name: String = "",
        var unique: String = "",
        var distinguishedName: String = "",
        var genderType: String = "",
        var employee: String = "",
        var mail: String = "",
        var weixin: String = "",
        var qq: String = "",
        var mobile: String = "",
        var officePhone: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(unique)
        writeString(distinguishedName)
        writeString(genderType)
        writeString(employee)
        writeString(mail)
        writeString(weixin)
        writeString(qq)
        writeString(mobile)
        writeString(officePhone)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<O2PersonPickerResultItem> = object : Parcelable.Creator<O2PersonPickerResultItem> {
            override fun createFromParcel(source: Parcel): O2PersonPickerResultItem = O2PersonPickerResultItem(source)
            override fun newArray(size: Int): Array<O2PersonPickerResultItem?> = arrayOfNulls(size)
        }
    }
}