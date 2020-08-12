package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fancy on 2017/4/25.
 */
sealed class NewContactListVO {

    class Department(
            var id: String = "",
            var name: String = "",
            var unique: String = "",
            var distinguishedName: String = "",
            var typeList: ArrayList<String> = ArrayList(),
            var shortName: String = "",
            var level: Int = -1,
            var levelName: String = "",
            var departmentCount: Int = 0,
            var identityCount: Int = 0) : NewContactListVO(), Parcelable {
        constructor(source: Parcel) : this(
                source.readString() ?: "",
                source.readString() ?: "",
                source.readString() ?: "",
                source.readString() ?: "",
                source.createStringArrayList() ?: ArrayList(),
                source.readString() ?: "",
                source.readInt(),
                source.readString() ?: "",
                source.readInt(),
                source.readInt()
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
            writeInt(departmentCount)
            writeInt(identityCount)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Department> = object : Parcelable.Creator<Department> {
                override fun createFromParcel(source: Parcel): Department = Department(source)
                override fun newArray(size: Int): Array<Department?> = arrayOfNulls(size)
            }
        }
    }

    class Identity(var id: String = "",
                   var name: String = "",
                   var person: String = "",
                   var distinguishedName: String = "",
                   var unit: String = "",
                   var unitName: String = "",
                   var unique: String = "",
                   var unitLevel: Int = -1,
                   var unitLevelName: String = ""
    ) : NewContactListVO(), Parcelable {
        constructor(source: Parcel) : this(
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
            writeString(person)
            writeString(distinguishedName)
            writeString(unit)
            writeString(unitName)
            writeString(unique)
            writeInt(unitLevel)
            writeString(unitLevelName)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Identity> = object : Parcelable.Creator<Identity> {
                override fun createFromParcel(source: Parcel): Identity = Identity(source)
                override fun newArray(size: Int): Array<Identity?> = arrayOfNulls(size)
            }
        }
    }

    //id 要用来分页查询的
    class Group(var id: String = "",
                var name: String = "",
                var distinguishedName: String = "",
                var unique: String = "") : NewContactListVO(), Parcelable {
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
            writeString(distinguishedName)
            writeString(unique)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Group> = object : Parcelable.Creator<Group> {
                override fun createFromParcel(source: Parcel): Group = Group(source)
                override fun newArray(size: Int): Array<Group?> = arrayOfNulls(size)
            }
        }
    }


    //id 要用来分页查询的
    class Person(var id: String = "",
                 var name: String = "",
                 var unique: String = "",
                 var distinguishedName: String = "",
                 var genderType: String = "",
                 var employee: String = "",
                 var mail: String = "",
                 var weixin: String = "",
                 var qq: String = "",
                 var mobile: String = "",
                 var officePhone: String = "") : NewContactListVO(), Parcelable {
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
            val CREATOR: Parcelable.Creator<Person> = object : Parcelable.Creator<Person> {
                override fun createFromParcel(source: Parcel): Person = Person(source)
                override fun newArray(size: Int): Array<Person?> = arrayOfNulls(size)
            }
        }
    }

}