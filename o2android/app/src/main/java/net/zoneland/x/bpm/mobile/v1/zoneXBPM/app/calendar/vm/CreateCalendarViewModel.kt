package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.vm

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarOB
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.CalendarPostData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CalendarPickerOption
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.FrontendResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by fancyLou on 11/07/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class CreateCalendarViewModel(app: Application) : BaseO2ViewModel(app) {


    val calendarId: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarTitle: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val calendarColor: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val comment: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isPublic: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val calendarType:  MutableLiveData<String> by lazy { MutableLiveData<String>() }
    //id 触发查询Calendar对象
    val oldCalendar: LiveData<CalendarPostData> = Transformations.switchMap(calendarId, { id ->
        val calendarData = MutableLiveData<CalendarPostData>()
        loadFromNet(id, calendarData)
        calendarData

    })

    val deleteBtnVisibleable: MediatorLiveData<Boolean> = MediatorLiveData()



    private val isLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    //网络操作反馈结果
    private val netResponse: MutableLiveData<FrontendResponse> by lazy { MutableLiveData<FrontendResponse>() }

    private val calendarTypeKey: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    init {
        isPublic.value = false
        deleteBtnVisibleable.addSource(calendarId, { id->
            deleteBtnVisibleable.value = !TextUtils.isEmpty(id)
        })
    }


    fun saveCalendar() {
        val post = CalendarPostData()
        post.name = calendarTitle.value ?:""
        post.type = calendarTypeKey.value ?:""
        post.color = calendarColor.value ?:""
        post.isPublic = isPublic.value ?:false
        post.status = "OPEN"
        post.description = comment.value ?:""
        post.target = O2SDKManager.instance().distinguishedName

        XLog.info("$post")
        isLoading.value = true
        getCalendarAssembleService()?.saveCalendar(post)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        isLoading.value = false
                        val res = FrontendResponse(true, "保存成功！")
                        netResponse.value = res
                    }
                    onError { e, isNetworkError ->
                        XLog.error("保存日历异常，isnet:$isNetworkError", e)
                        isLoading.value = false
                        val res = FrontendResponse(false, "保存失败！")
                        netResponse.value = res
                    }
                }
    }

    fun updateCalendar() {
        if (TextUtils.isEmpty(calendarId.value)) {
            val res = FrontendResponse(false, "更新异常，没有Id！")
            netResponse.value = res
            return
        }
        val post = CalendarPostData()
        post.id = calendarId.value!!
        post.name = calendarTitle.value ?:""
        post.type = calendarTypeKey.value ?:""
        post.color = calendarColor.value ?:""
        post.isPublic = isPublic.value ?:false
        post.status = "OPEN"
        post.description = comment.value ?:""
        post.target = O2SDKManager.instance().distinguishedName
        XLog.info("update calendar: $post")
        isLoading.value = true
        getCalendarAssembleService()?.saveCalendar(post)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        isLoading.value = false
                        val res = FrontendResponse(true, "更新成功！")
                        netResponse.value = res
                    }
                    onError { e, isNetworkError ->
                        XLog.error("保存日历异常，isnet:$isNetworkError", e)
                        isLoading.value = false
                        val res = FrontendResponse(false, "更新失败！")
                        netResponse.value = res
                    }
                }
    }

    fun deleteCalendar() {
        if (TextUtils.isEmpty(calendarId.value)) {
            val res = FrontendResponse(false, "删除异常，没有Id！")
            netResponse.value = res
            return
        }
        getCalendarAssembleService()?.deleteCalendar(calendarId.value!!)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.o2Subscribe {
                    onNext {
                        isLoading.value = false
                        val res = FrontendResponse(true, "删除成功！")
                        netResponse.value = res
                    }
                    onError { e, isNetworkError ->
                        XLog.error("删除日历异常，isnet:$isNetworkError", e)
                        isLoading.value = false
                        val res = FrontendResponse(false, "删除失败！")
                        netResponse.value = res
                    }
                }
    }


    fun calendarColorLive(): LiveData<String> = calendarColor

    fun isLoadingLive(): LiveData<Boolean> = isLoading

    fun netResponseLive(): LiveData<FrontendResponse> = netResponse


    fun setCalendarType(type: CalendarPickerOption) {
        calendarTypeKey.value = type.value
        calendarType.value = type.name
    }

    fun setBackCalendarInfo(info: CalendarPostData) {
        calendarTitle.value = info.name
        var type = CalendarOB.calendarTypes[info.type]
        if (type == null) {
            type = CalendarOB.calendarTypes["PERSON"]!!
        }
        setCalendarType(CalendarPickerOption(type, info.type))
        calendarColor.value = info.color
        isPublic.value = info.isPublic
        comment.value = info.description
    }


    private fun loadFromNet(id: String?, calendarPostData: MutableLiveData<CalendarPostData>) {
        if (!TextUtils.isEmpty(id)) {
            val service = getCalendarAssembleService()
            if (service!=null) {
                service.getCalendar(id!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .o2Subscribe {
                            onNext { res ->
                                val calendar = res.data
                                calendarPostData.value = calendar
                            }
                            onError { e, isNetworkError ->
                                XLog.error("查询日历出错， isnetworkError:$isNetworkError", e)
                                calendarPostData.value = null
                            }
                        }
            }
        }
    }
}