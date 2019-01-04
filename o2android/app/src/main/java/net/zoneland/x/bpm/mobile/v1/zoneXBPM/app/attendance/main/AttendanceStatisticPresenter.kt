package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenterImpl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailQueryFilterJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceStatisticGroupHeader
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 28/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

class AttendanceStatisticPresenter : BasePresenterImpl<AttendanceStatisticContract.View>(), AttendanceStatisticContract.Presenter {

    override fun getAttendanceStatisticCycle(query: String) {
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            val year = query.substring(0, 4)
            val month = query.substring(5, 7)
            service.myAttendanceStatisticCycle(year, month)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { result ->
                            var message = ""
                            if (result.data != null) {
                                message = "考勤周期："+result.data.cycleStartDateString + "至" + result.data.cycleEndDateString
                            }
                            mView?.attendanceStatisticCycle(message)
                        }
                        onError { e, isNetworkError ->
                            XLog.error("获取考勤统计周期异常, isneterror:$isNetworkError", e)
                            mView?.attendanceStatisticCycle("")
                        }
                    }
        }
    }

    override fun getAttendanceListByMonth(query: String) {
        var lateNumber = 0
        var earlierNumber = 0
        var absentNumber = 0
        var normalNumber = 0
        getAttendanceAssembleControlService(mView?.getContext())?.let { service ->
            val year = query.substring(0, 4)
            val month = query.substring(5, 7)
            val filter = AttendanceDetailQueryFilterJson(cycleYear = year, cycleMonth = month, q_empName = O2SDKManager.instance().distinguishedName, key = "recordDateString")
            service.myAttendanceDetailListByMonth(filter, O2.FIRST_PAGE_TAG, "100")
                    .subscribeOn(Schedulers.io())
                    .flatMap { result ->
                        val list = ArrayList<Group<AttendanceStatisticGroupHeader, AttendanceDetailInfoJson>>()
                        val map = HashMap<Int, ArrayList<AttendanceDetailInfoJson>>()
                        result.data.map { detail ->
                            when {
                                detail.isLate -> {
                                    if (map.containsKey(0)) {
                                        map[0]?.add(detail)
                                    } else {
                                        map[0] = arrayListOf(detail)
                                    }
                                    lateNumber++
                                }
                                detail.isLeaveEarlier -> {
                                    if (map.containsKey(1)) {
                                        map[1]?.add(detail)
                                    } else {
                                        map[1] = arrayListOf(detail)
                                    }
                                    earlierNumber++
                                }
                                detail.isAbsent -> {
                                    if (map.containsKey(2)) {
                                        map[2]?.add(detail)
                                    } else {
                                        map[2] = arrayListOf(detail)
                                    }
                                    absentNumber++
                                }
                                else -> {
                                    if (map.containsKey(3)) {
                                        map[3]?.add(detail)
                                    } else {
                                        map[3] = arrayListOf(detail)
                                    }
                                    normalNumber++
                                }
                            }
                        }
                        val lateList = map[0]
                        if (lateList != null && lateList.isNotEmpty()) {
                            val newlist = lateList.sortedBy { it.recordDateString }
                            val header = AttendanceStatisticGroupHeader(0, newlist[0])
                            if (newlist.size > 1) {
                                val group = Group(header, newlist.subList(1, newlist.size))
                                list.add(group)
                            } else {
                                val group = Group(header, ArrayList<AttendanceDetailInfoJson>())
                                list.add(group)
                            }
                        }
                        val earlierList = map[1]
                        if (earlierList != null && earlierList.isNotEmpty()) {
                            val newlist = earlierList.sortedBy { it.recordDateString }
                            val header = AttendanceStatisticGroupHeader(1, newlist[0])
                            if (newlist.size > 1) {
                                val group = Group(header, newlist.subList(1, newlist.size))
                                list.add(group)
                            } else {
                                val group = Group(header, ArrayList<AttendanceDetailInfoJson>())
                                list.add(group)
                            }
                        }
                        val absentList = map[2]
                        if (absentList != null && absentList.isNotEmpty()) {
                            val newlist = absentList.sortedBy { it.recordDateString }
                            val header = AttendanceStatisticGroupHeader(2, newlist[0])
                            if (newlist.size > 1) {
                                val group = Group(header, newlist.subList(1, newlist.size))
                                list.add(group)
                            } else {
                                val group = Group(header, ArrayList<AttendanceDetailInfoJson>())
                                list.add(group)
                            }
                        }
                        val normalList = map[3]
                        if (normalList != null && normalList.isNotEmpty()) {
                            val newlist = normalList.sortedBy { it.recordDateString }
                            val header = AttendanceStatisticGroupHeader(3, newlist[0])
                            if (newlist.size > 1) {
                                val group = Group(header, newlist.subList(1, newlist.size))
                                list.add(group)
                            } else {
                                val group = Group(header, ArrayList<AttendanceDetailInfoJson>())
                                list.add(group)
                            }
                        }
                        XLog.info("isLate:$lateNumber, earlier:$earlierNumber, absent:$absentNumber, normal:$normalNumber, listSize:${list.size}")
                        Observable.just(list)
                    }.observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { list ->
                            mView?.attendanceDetailList(list, lateNumber, earlierNumber, absentNumber, normalNumber)
                        }
                        onError { e, isNetworkError ->
                            XLog.error("获取考勤数据异常，isnetworkerror:$isNetworkError", e)
                            mView?.attendanceDetailList(emptyList(), 0, 0, 0, 0)
                        }
                    }
        }

    }
}