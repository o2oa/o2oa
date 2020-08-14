package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fancyLou on 14/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */
data class CalendarEventInfoData(
        var createTime: String = "",
        var updateTime: String = "",
        var sequence: String = "",
        var id: String = "",
        var calendarId: String = "",
        var repeatMasterId: String = "",
        var eventType: String = "",
        var title: String = "",
        var color: String = "",
        var comment: String = "",
        var startTime: String = "",
        var startTimeStr: String = "",
        var endTime: String = "",
        var endTimeStr: String = "",
        var locationName: String = "",
        var recurrenceRule: String = "",
        var alarm: Boolean = false,
        var alarmTime: String = "",
        var alarmAlready: Boolean = false,
        var valarmTime_config: String = "",
        var valarm_Summary: String = "",
        var isAllDayEvent: Boolean = false,
        var daysOfDuration: Int = 0,
        var isPublic: Boolean = false,
        var source: String = "",
        var createPerson: String = "",
        var updatePerson: String = "",
        var targetType: String = "",
        var participants: ArrayList<String> = ArrayList(),
        var manageablePersonList: ArrayList<String> = ArrayList(),
        var viewablePersonList: ArrayList<String> = ArrayList(),
        var viewableUnitList: ArrayList<String> = ArrayList(),
        var viewableGroupList: ArrayList<String> = ArrayList()
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
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            1 == source.readInt(),
            source.readString() ?: "",
            1 == source.readInt(),
            source.readString() ?: "",
            source.readString() ?: "",
            1 == source.readInt(),
            source.readInt(),
            1 == source.readInt(),
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.readString() ?: "",
            source.createStringArrayList()?: ArrayList(),
            source.createStringArrayList()?: ArrayList(),
            source.createStringArrayList()?: ArrayList(),
            source.createStringArrayList()?: ArrayList(),
            source.createStringArrayList()?: ArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(createTime)
        writeString(updateTime)
        writeString(sequence)
        writeString(id)
        writeString(calendarId)
        writeString(repeatMasterId)
        writeString(eventType)
        writeString(title)
        writeString(color)
        writeString(comment)
        writeString(startTime)
        writeString(startTimeStr)
        writeString(endTime)
        writeString(endTimeStr)
        writeString(locationName)
        writeString(recurrenceRule)
        writeInt((if (alarm) 1 else 0))
        writeString(alarmTime)
        writeInt((if (alarmAlready) 1 else 0))
        writeString(valarmTime_config)
        writeString(valarm_Summary)
        writeInt((if (isAllDayEvent) 1 else 0))
        writeInt(daysOfDuration)
        writeInt((if (isPublic) 1 else 0))
        writeString(source)
        writeString(createPerson)
        writeString(updatePerson)
        writeString(targetType)
        writeStringList(participants)
        writeStringList(manageablePersonList)
        writeStringList(viewablePersonList)
        writeStringList(viewableUnitList)
        writeStringList(viewableGroupList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CalendarEventInfoData> = object : Parcelable.Creator<CalendarEventInfoData> {
            override fun createFromParcel(source: Parcel): CalendarEventInfoData = CalendarEventInfoData(source)
            override fun newArray(size: Int): Array<CalendarEventInfoData?> = arrayOfNulls(size)
        }
    }
}